package forex.domain.forge

import io.circe._
import io.circe.generic.semiauto._

case class Conversion(value: BigDecimal, text: String, timestamp: Long)

object Conversion {
  implicit val decoder: Decoder[Conversion] = deriveDecoder[Conversion]
}
