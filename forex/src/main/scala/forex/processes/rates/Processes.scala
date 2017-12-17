package forex.processes.rates

import cats.Monad
import cats.data.EitherT
import cats.implicits._
import forex.domain._
import forex.processes.RatesError
import forex.services.ServiceError.UnexpectedError
import forex.services.{ Apply, _ }
import io.circe.parser.decode
import io.circe.syntax._

object Processes {
  def apply[F[_]]: Processes[F] =
    new Processes[F] {}
}

trait Processes[F[_]] {
  import converters._

  def get(
      request: GetRequest
  )(
      implicit
      M: Monad[F],
      OneForge: OneForge[F],
      Cache: Cache[F],
      Apply: Apply[F],
  ): F[RatesError Either Rate] = {
    val result = for {
      key ← EitherT(Apply.apply[Either[ServiceError, String]](Right(s"${request.from}${request.to}")))
        .leftMap(toProcessError)
      cacheResult ← EitherT(Cache.get(key))
        .leftMap(toProcessError)
      result ← EitherT {
        cacheResult match {
          case None ⇒
            getAndSet(request)
          case Some(s) ⇒ Apply.apply[Either[ServiceError, Rate]](toRate(s))
        }
      }.leftMap(toProcessError)
    } yield result

    result.value
  }

  private def getAndSet(
      request: GetRequest
  )(implicit M: Monad[F], OneForge: OneForge[F], Cache: Cache[F]): F[Either[ServiceError, Rate]] = {
    val result = for {
      rate ← EitherT(OneForge.get(Rate.Pair(request.from, request.to)))
      _ ← EitherT(Cache.put(s"${request.from}${request.to}", rate.asJson.noSpaces))
    } yield {
      rate
    }

    result.value
  }

  def toRate(s: String): Either[ServiceError, Rate] =
    decode[Rate](s)
      .fold(fa ⇒ Left(UnexpectedError(s"Can not decode $s into Rate", fa.fillInStackTrace().some)), fb ⇒ Right(fb))
}
