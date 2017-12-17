package forex.processes.rates

import forex.processes._
import forex.services._

package object converters {

  def toProcessError[T <: Throwable](t: T): ProcessError = t match {
    case ServiceError.ForgeError(code, reason, throwable) ⇒
      ProcessError.ForgeError(code, reason, throwable)
    case ServiceError.CacheError(throwable) ⇒
      ProcessError.CacheError(throwable)
    case ServiceError.UnexpectedError(reason, throwable) ⇒
      ProcessError.UnexpectedError(reason, throwable)
    case e: ProcessError ⇒ e
  }

}
