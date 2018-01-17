package forex.interfaces.api.utils

import akka.http.scaladsl._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ RejectionHandler, _ }
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import io.circe.{ Decoder, Encoder }

final case class RejectionResponse(reason: String)
object RejectionResponse {
  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  implicit val encoder: Encoder[RejectionResponse] =
    deriveEncoder[RejectionResponse]

  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  implicit val decoder: Decoder[RejectionResponse] =
    deriveDecoder[RejectionResponse]
}

object ApiRejectionHandler extends LazyLogging {

  def apply(): server.RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handle {
        case MalformedQueryParamRejection(name, msg, _) ⇒
          val response =
            RejectionResponse(s"The query parameter '$name' was malformed:$msg")
          complete((StatusCodes.BadRequest, response))
      }
      .handle {
        case MissingQueryParamRejection(paramName) ⇒
          val response =
            RejectionResponse(s"Request is missing required query parameter '$paramName'")
          complete((NotFound, response))
      }
      .handleAll[MethodRejection] { methodRejections ⇒
        val names = methodRejections.map(_.supported.name)
        val response = RejectionResponse(s"HTTP method not allowed, supported methods: ${names.mkString(", ")}")
        complete((StatusCodes.MethodNotAllowed, response))
      }
      .handleNotFound {
        val response =
          RejectionResponse("The requested resource could not be found.")
        complete((StatusCodes.NotFound, response))
      }
      .result()
}
