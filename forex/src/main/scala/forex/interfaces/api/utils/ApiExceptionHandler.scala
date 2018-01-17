package forex.interfaces.api.utils

import akka.http.scaladsl._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import forex.processes._
import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import io.circe.{ Decoder, Encoder }

final case class ExceptionResponse(reason: String, throwableMessage: String)
object ExceptionResponse {
  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  implicit val encoder: Encoder[ExceptionResponse] =
    deriveEncoder[ExceptionResponse]

  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  implicit val decoder: Decoder[ExceptionResponse] =
    deriveDecoder[ExceptionResponse]
}

object ApiExceptionHandler extends LazyLogging {

  def apply(): server.ExceptionHandler =
    server.ExceptionHandler {
      case f: RatesError.ForgeError ⇒
        val response = ExceptionResponse(s"forge service error: ${f.reason}", f.throwable.fold("")(_.getMessage))
        logger.error("forge service error", f.throwable)
        complete((StatusCodes.InternalServerError, response))

      case c: RatesError.CacheError ⇒
        val response = ExceptionResponse("cache error:", c.throwable.getMessage)
        logger.error("cache error", c.throwable)
        complete((StatusCodes.InternalServerError, response))

      case u: RatesError.UnexpectedError ⇒
        val response = ExceptionResponse(s"unexpected error: ${u.reason}", u.throwable.fold("")(_.getMessage))
        logger.error("unexpected error", u.throwable)
        complete((StatusCodes.InternalServerError, response))

      case t: Throwable ⇒
        val response =
          ExceptionResponse(s"unexpected error", t.getMessage)
        logger.error("unexpected error", t)
        complete((StatusCodes.InternalServerError, response))
    }

}
