package forex.domain.forge

import io.circe._
import io.circe.generic.semiauto._

case class Error(error: Boolean, message: String)

object Error {
  implicit val decoder: Decoder[Error] = deriveDecoder[Error]
}
