package forex

package object services {
  type OneForge[F[_]] = oneforge.Algebra[F]
  final val OneForge = oneforge.Interpreters

  type Cache[F[_]] = cache.Algebra[F]
  final val Cache = cache.Interpreters

  type Apply[F[_]] = apply.Algebra[F]
  final val Apply = apply.Interpreters

}
