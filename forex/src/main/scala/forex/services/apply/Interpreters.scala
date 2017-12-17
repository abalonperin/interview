package forex.services.apply

import monix.eval.Task
import org.atnos.eff._
import org.atnos.eff.addon.monix.task._

object Interpreters {
  def monixTask[R](
      implicit
      m1: _task[R]
  ): Algebra[Eff[R, ?]] = new MonixTask[R]
}

final class MonixTask[R] private[apply] (
    implicit
    m1: _task[R]
) extends Algebra[Eff[R, ?]] {
  def apply[A](a: ⇒ A): Eff[R, A] =
    fromTask(Task.now(a))
}
