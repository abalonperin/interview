package forex.processes

import scala.util.control.NoStackTrace

import forex.services.ServiceError

sealed trait ProcessError extends Throwable with NoStackTrace

object ProcessError {
  final case class ForgeError(statusCode: Int, reason: String, throwable: Option[Throwable] = None) extends ProcessError

  final case class CacheError(throwable: Throwable) extends ProcessError

  final case class UnexpectedError(reason: String, throwable: Option[Throwable] = None) extends ProcessError
}
