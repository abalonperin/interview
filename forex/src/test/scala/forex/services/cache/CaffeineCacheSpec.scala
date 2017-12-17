package forex.services.cache

import cats.data.EitherT
import cats.implicits._
import forex.config.CacheConfig
import forex.services.ServiceError
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.atnos.eff.addon.monix.task._
import org.atnos.eff.syntax.addon.monix.task._
import org.atnos.eff.{ Eff, Fx1 }
import org.scalatest.{ AsyncFlatSpec, Matchers }
import eu.timepit.refined.auto._
import scala.concurrent.duration._
import scala.util.Random

import com.github.blemale.scaffeine.Scaffeine

class CaffeineCacheSpec extends AsyncFlatSpec with Matchers {

  "CaffeineCache" should "get a value and value never expires" in {
    val test = s"""{"name":"test${Random.nextInt()}"}"""

    val cache =
      Interpreters.caffeineCache(CacheConfig(1000, Long.MaxValue nanoseconds))

    val result = for {
      _ ← EitherT(cache.put("key", test))
      r ← EitherT(cache.get("key"))
      _ ← EitherT(cache.delete("key"))
    } yield { r }

    result.value.runAsync.runAsync
      .map(_.fold(_ ⇒ assert(false), fb ⇒ {
        fb shouldBe Some(test)
      }))
  }

  it should "not get a value and value never expires" in {
    val cache =
      Interpreters.caffeineCache(CacheConfig(1000, Long.MaxValue nanoseconds))

    val result = for {
      r ← EitherT(cache.get("key"))
    } yield { r }

    result.value.runAsync.runAsync
      .map(_.fold(_ ⇒ assert(false), fb ⇒ {
        fb shouldBe None
      }))
  }

  it should "get None when the value has expired" in {
    val test = s"""{"name":"test${Random.nextInt()}"}"""

    val cache = Interpreters.caffeineCache(CacheConfig(1000, 1 seconds))

    val result = for {
      _ ← EitherT(cache.put("key", test))
      _ ← EitherT(sleep(2000))
      r ← EitherT(cache.get("key"))
      _ ← EitherT(cache.delete("key"))
    } yield { r }

    result.value.runAsync.runAsync
      .map(_.fold(_ ⇒ assert(false), fb ⇒ {
        fb shouldBe None
      }))
  }

  it should "get a value when the value has not expired" in {
    val test = s"""{"name":"test${Random.nextInt()}"}"""

    val cache = Interpreters.caffeineCache(CacheConfig(1000, 5 seconds))

    val result = for {
      _ ← EitherT(cache.put("key", test))
      _ ← EitherT(sleep(1000))
      r ← EitherT(cache.get("key"))
      _ ← EitherT(cache.delete("key"))
    } yield { r }

    result.value.runAsync.runAsync
      .map(_.fold(_ ⇒ assert(false), fb ⇒ {
        fb shouldBe Some(test)
      }))
  }

  private def sleep(sleepTimeInMilliSecond: Long): Eff[Fx1[Task], Either[ServiceError, Unit]] =
    for {
      _ ← fromTask(Task.now(Thread.sleep(sleepTimeInMilliSecond)))
    } yield {
      Right(())
    }
}
