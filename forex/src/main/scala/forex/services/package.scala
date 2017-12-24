package forex

import forex.services.oneforge.Interpreters

package object services {
  type OneForge[F[_]] = oneforge.Algebra[F]
  val OneForge: Interpreters.type = oneforge.Interpreters

  type Cache[F[_]] = cache.Algebra[F]
  val Cache: cache.Interpreters.type = cache.Interpreters

  type Apply[F[_]] = apply.Algebra[F]
  val Apply: apply.Interpreters.type = apply.Interpreters

}
