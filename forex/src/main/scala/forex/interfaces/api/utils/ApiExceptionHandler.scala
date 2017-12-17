package forex.interfaces.api.utils

import akka.http.scaladsl._
import forex.processes._

object ApiExceptionHandler {

  def apply(): server.ExceptionHandler =
    server.ExceptionHandler {
      case f: RatesError.ForgeError ⇒
        ctx ⇒
          ctx.complete(
            s""""Something went wrong in the forge service: {"statusCode":${f.statusCode}, "reason":"${f.reason}", "throwable":"${f.throwable}"}"""
          )
      case c: RatesError.CacheError ⇒
        ctx ⇒
          ctx.complete(s"""Something went wrong in the cache service: {"throwable":"${c.toString}""")
      case t: Throwable ⇒
        ctx ⇒
          ctx.complete(s"Something else went wrong: $t")
    }

}
