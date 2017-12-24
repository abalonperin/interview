package forex.domain.forge

import io.circe._
import io.circe.generic.semiauto._

final case class Conversion(value: BigDecimal, text: String, timestamp: Long)

object Conversion {
  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  implicit val decoder: Decoder[Conversion] = deriveDecoder[Conversion]
}
