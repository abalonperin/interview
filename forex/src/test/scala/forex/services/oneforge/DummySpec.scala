package forex.services.oneforge

import cats.data.EitherT
import cats.implicits._
import forex.domain.{ Currency, Rate, Timestamp }
import monix.execution.Scheduler.Implicits.global
import org.atnos.eff.syntax.addon.monix.task._
import org.scalatest.{ AsyncFlatSpec, Matchers }

class DummySpec extends AsyncFlatSpec with Matchers {

  "Dummy" should "get a dummy rate" in {
    val pair = Rate.Pair(Currency.USD, Currency.JPY)

    val value = for {
      r ← EitherT(
        Interpreters.dummy
          .get(pair)
      )
    } yield { r }

    value.value.runAsync.runAsync
      .map(_.fold(_ ⇒ assert(false), fb ⇒ {
        fb.pair shouldBe pair
        fb.price.value shouldBe BigDecimal(100)
        fb.timestamp.value should be < Timestamp.now.value
      }))
  }
}
