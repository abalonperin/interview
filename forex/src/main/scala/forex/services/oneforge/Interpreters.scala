package forex.services.oneforge

import java.nio.ByteBuffer

import cats.implicits._
import com.softwaremill.sttp._
import forex.config.ForgeConfig
import forex.domain._
import forex.services.ServiceError
import monix.eval.Task
import monix.reactive.Observable
import org.atnos.eff._
import org.atnos.eff.addon.monix.task.{ fromTask, _ }

object Interpreters {
  def dummy[R](
      implicit
      m1: _task[R]
  ): Algebra[Eff[R, ?]] = new Dummy[R]

  def live[R](forgeConfig: ForgeConfig)(
      implicit
      m1: _task[R],
      backend: SttpBackend[Task, Observable[ByteBuffer]]
  ): Algebra[Eff[R, ?]] = new Live[R](forgeConfig)
}

final class Dummy[R] private[oneforge] (
    implicit
    m1: _task[R]
) extends Algebra[Eff[R, ?]] {
  override def get(
      pair: Rate.Pair
  ): Eff[R, Either[ServiceError, Rate]] =
    for {
      result ← fromTask(Task.now(Rate(pair, Price(BigDecimal(100)), Timestamp.now)))
    } yield Right(result)
}

final class Live[R] private[oneforge] (forgeConfig: ForgeConfig)(
    implicit
    m1: _task[R],
    backend: SttpBackend[Task, Observable[ByteBuffer]]
) extends Algebra[Eff[R, ?]] {
  override def get(
      pair: Rate.Pair
  ): Eff[R, Either[ServiceError, Rate]] = {

    val uri: Uri =
      uri"${forgeConfig.url}?from=${pair.from}&to=${pair.to}&quantity=1&api_key=${forgeConfig.key}"

    for {
      r ← fromTask(
        forgeConfig.readTimeOut.fold(
          sttp
            .get(uri)
            .send()
        )(
          f ⇒
            sttp
              .get(uri)
              .readTimeout(f)
              .send()
        )
      )
    } yield r.toRate(pair)

  }
}
