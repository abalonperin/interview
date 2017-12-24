package forex.domain

import java.time.OffsetDateTime

import io.circe._
import io.circe.java8.time._
import io.circe.generic.extras.wrapped._

final case class Timestamp(value: OffsetDateTime) extends AnyVal

object Timestamp {
  def now: Timestamp =
    Timestamp(OffsetDateTime.now)

  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  implicit val encoder: Encoder[Timestamp] =
    deriveUnwrappedEncoder[Timestamp]

  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  implicit val decoder: Decoder[Timestamp] = deriveUnwrappedDecoder[Timestamp]
}
