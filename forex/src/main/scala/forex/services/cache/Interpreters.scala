package forex.services.cache

import scala.collection.mutable

import cats.implicits._
import com.github.blemale.scaffeine
import com.github.blemale.scaffeine.Scaffeine
import forex.config.CacheConfig
import forex.services.ServiceError
import monix.eval.Task
import org.atnos.eff._
import org.atnos.eff.addon.monix.task._

object Interpreters {
  def mapCache[R](
      implicit
      m1: _task[R]
  ): Algebra[Eff[R, ?]] = new MapCache[R]

  def caffeineCache[R](cacheConfig: forex.config.CacheConfig)(
      implicit
      m1: _task[R]
  ): Algebra[Eff[R, ?]] = new CaffeineCache[R](Cache.caffeineCache(cacheConfig))
}

object Cache {
  @SuppressWarnings(Array("org.wartremover.warts.MutableDataStructures"))
  val mapCache: mutable.Map[String, String] = scala.collection.mutable.Map.empty

  def caffeineCache(cacheConfig: CacheConfig): scaffeine.Cache[String, String] =
    Scaffeine()
      .recordStats()
      .expireAfterWrite(cacheConfig.timeToLive)
      .maximumSize(cacheConfig.maximumSize.value)
      .build[String, String]()
}

final class MapCache[R] private[cache] (
    implicit
    m1: _task[R]
) extends Algebra[Eff[R, ?]] {

  private val cache: mutable.Map[String, String] = Cache.mapCache

  def get(key: String): Eff[R, Either[ServiceError, Option[String]]] =
    for {
      r ← fromTask(Task.now(cache.get(key)))
    } yield {
      Right(r)
    }

  def put(key: String, value: String): Eff[R, Either[ServiceError, Unit]] =
    for {
      _ ← fromTask(Task.now(cache.put(key, value)))
    } yield {
      Right(())
    }

  def delete(key: String): Eff[R, Either[ServiceError, Unit]] =
    for {
      _ ← fromTask(Task.now(cache.remove(key)))
    } yield {
      Right(())
    }
}

final class CaffeineCache[R] private[cache] (cache: scaffeine.Cache[String, String])(implicit
                                                                                     m1: _task[R])
    extends Algebra[Eff[R, ?]] {

  def get(key: String): Eff[R, Either[ServiceError, Option[String]]] =
    for {
      r ← fromTask(Task(toEither(cache.getIfPresent(key))))
    } yield {
      r
    }

  def put(key: String, value: String): Eff[R, Either[ServiceError, Unit]] =
    for {
      r ← fromTask(Task(toEither(cache.put(key, value))))
    } yield {
      r
    }

  def delete(key: String): Eff[R, Either[ServiceError, Unit]] =
    for {
      r ← fromTask(Task(toEither(cache.invalidate(key))))
    } yield {
      r
    }

  private def toEither[A](a: ⇒ A): Either[ServiceError, A] =
    try {
      Right(a)
    } catch {
      case e: Throwable ⇒ Left(forex.services.ServiceError.CacheError(e))
    }
}
