package forex.services

import scala.util.control.NoStackTrace

sealed trait ServiceError extends Throwable with NoStackTrace

object ServiceError {
  final case class ForgeError(statusCode: Int, reason: String, throwable: Option[Throwable] = None) extends ServiceError

  final case class CacheError(throwable: Throwable) extends ServiceError

  final case class UnexpectedError(reason: String, throwable: Option[Throwable] = None) extends ServiceError
}
