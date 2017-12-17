package forex.services.oneforge

import forex.domain._
import forex.services.ServiceError

trait Algebra[F[_]] {
  def get(pair: Rate.Pair): F[ServiceError Either Rate]
}
