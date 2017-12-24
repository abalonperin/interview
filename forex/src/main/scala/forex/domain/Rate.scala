package forex.domain

import io.circe._
import io.circe.generic.semiauto._

final case class Rate(
    pair: Rate.Pair,
    price: Price,
    timestamp: Timestamp
)

object Rate {
  final case class Pair(
      from: Currency,
      to: Currency
  )

  object Pair {
    @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
    implicit val encoder: Encoder[Pair] =
      deriveEncoder[Pair]

    @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
    implicit val decoder: Decoder[Pair] =
      deriveDecoder[Pair]
  }

  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  implicit val encoder: Encoder[Rate] =
    deriveEncoder[Rate]

  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  implicit val decoder: Decoder[Rate] = deriveDecoder[Rate]
}
