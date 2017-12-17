package forex.services.cache

import cats.data.EitherT
import cats.implicits._
import monix.execution.Scheduler.Implicits.global
import org.atnos.eff.syntax.addon.monix.task._
import org.scalatest.{ AsyncFlatSpec, Matchers }

class MapCacheSpec extends AsyncFlatSpec with Matchers {

  "MapCacheSpec" should "get a value" in {
    val test = """{"name":"test"}"""

    val result = for {
      _ ← EitherT(Interpreters.mapCache.put("key", test))
      r ← EitherT(Interpreters.mapCache.get("key"))
      _ ← EitherT(Interpreters.mapCache.delete("key"))
    } yield { r }

    result.value.runAsync.runAsync
      .map(_.fold(_ ⇒ assert(false), fb ⇒ {
        fb shouldBe Some(test)
      }))
  }

  it should "not get a value" in {
    val result = for {
      r ← EitherT(Interpreters.mapCache.get("key"))
    } yield { r }

    result.value.runAsync.runAsync
      .map(_.fold(_ ⇒ assert(false), fb ⇒ {
        fb shouldBe None
      }))
  }
}
