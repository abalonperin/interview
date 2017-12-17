package forex.services.apply

trait Algebra[F[_]] {
  def apply[A](a: ⇒ A): F[A]
}
