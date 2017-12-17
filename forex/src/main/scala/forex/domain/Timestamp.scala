package forex.domain

import java.time.OffsetDateTime

import io.circe._
import io.circe.java8.time._
import io.circe.generic.extras.wrapped._

case class Timestamp(value: OffsetDateTime) extends AnyVal

object Timestamp {
  def now: Timestamp =
    Timestamp(OffsetDateTime.now)

  implicit val encoder: Encoder[Timestamp] =
    deriveUnwrappedEncoder[Timestamp]

  implicit val decoder: Decoder[Timestamp] = deriveUnwrappedDecoder[Timestamp]
}
