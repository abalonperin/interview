package forex.interfaces.api.utils

import akka.http.scaladsl._
import akka.http.scaladsl.model._
import forex.processes._

final case class ErrorRespone(reason: String, throwable: Throwable)

object ApiExceptionHandler {

  def apply(): server.ExceptionHandler =
    server.ExceptionHandler {
      case f: RatesError.ForgeError ⇒
        ctx ⇒
          ctx.complete(
            HttpResponse(
              StatusCodes.InternalServerError,
              entity = HttpEntity(
                s"""{"reason":"forger service error: ${f.reason}", "throwable":"${f.throwable}"}"""
              )
            )
          )
      case c: RatesError.CacheError ⇒
        ctx ⇒
          ctx.complete(
            HttpResponse(
              StatusCodes.InternalServerError,
              entity = HttpEntity(s"""{"reason":"cache error", "throwable":"${c.throwable}"}""")
            )
          )
      case u: RatesError.UnexpectedError ⇒
        ctx ⇒
          ctx.complete(
            HttpResponse(
              StatusCodes.InternalServerError,
              entity = HttpEntity(s"""{"reason":"unexpected error", "throwable":"${u}"}""")
            )
          )
      case t: Throwable ⇒
        ctx ⇒
          ctx.complete(
            HttpResponse(
              StatusCodes.InternalServerError,
              entity = HttpEntity(s"""{"reason":"unexpected error", "throwable":"${t}"}""")
            )
          )
    }

}
