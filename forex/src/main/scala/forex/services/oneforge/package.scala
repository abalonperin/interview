package forex.services

import java.time.{ Instant, OffsetDateTime, ZoneId }

import cats.implicits._
import com.softwaremill.sttp.Response
import forex.domain.forge.Conversion
import forex.domain.{ Price, Rate, Timestamp }
import forex.services.ServiceError.ForgeError
import io.circe.parser.decode

package object oneforge {
  implicit class e(response: Response[String]) {
    def toRate(pair: Rate.Pair): Either[ForgeError, Rate] =
      response.body match {
        case Right(r) ⇒
          (decode[Conversion](r), decode[forex.domain.forge.Error](r)) match {
            case (Right(c), _) ⇒
              Right(
                Rate(
                  pair,
                  Price(c.value),
                  Timestamp(OffsetDateTime.ofInstant(Instant.ofEpochMilli(c.timestamp * 1000), ZoneId.of("UTC")))
                )
              )
            case (Left(_), Right(e)) ⇒
              Left(ForgeError(response.code, e.message))
            case (Left(l), Left(_)) ⇒
              Left(ForgeError(response.code, s"Unexpected error: $r", l.fillInStackTrace().some))
          }
        case Left(l) ⇒
          Left(ForgeError(response.code, s"Unexpected error: $l"))
      }
  }
}
