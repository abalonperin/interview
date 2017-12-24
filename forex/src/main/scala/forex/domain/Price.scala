package forex.domain

import io.circe._
import io.circe.generic.extras.wrapped._

final case class Price(value: BigDecimal) extends AnyVal

object Price {
  def apply(value: Integer): Price =
    Price(BigDecimal(value))

  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  implicit val encoder: Encoder[Price] = deriveUnwrappedEncoder[Price]

  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  implicit val decoder: Decoder[Price] = deriveUnwrappedDecoder[Price]
}
