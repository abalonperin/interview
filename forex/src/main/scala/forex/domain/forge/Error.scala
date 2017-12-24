package forex.domain.forge

import io.circe._
import io.circe.generic.semiauto._

final case class Error(error: Boolean, message: String)

object Error {
  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  implicit val decoder: Decoder[Error] = deriveDecoder[Error]
}
