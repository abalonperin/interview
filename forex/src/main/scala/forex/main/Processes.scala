package forex.main

import java.nio.ByteBuffer

import com.softwaremill.sttp.asynchttpclient.monix.AsyncHttpClientMonixBackend
import com.softwaremill.sttp.{ SttpBackend, SttpBackendOptions }
import forex.config._
import forex.{ processes ⇒ p, services ⇒ s }
import monix.eval.Task
import monix.reactive.Observable
import org.zalando.grafter.macros._

@readerOf[ApplicationConfig]
case class Processes(forgeConfig: ForgeConfig, cacheConfig: CacheConfig) {

  implicit val sttpBackend: SttpBackend[Task, Observable[ByteBuffer]] =
    forgeConfig.connectionTimeOut.fold(AsyncHttpClientMonixBackend())(
      f ⇒ AsyncHttpClientMonixBackend(SttpBackendOptions.connectionTimeout(f))
    )

  implicit final lazy val _oneForge: s.OneForge[AppEffect] =
    s.OneForge.live[AppStack](forgeConfig)

  implicit final lazy val _cache: s.Cache[AppEffect] =
    s.Cache.caffeineCache[AppStack](cacheConfig)

  implicit final lazy val _apply: s.Apply[AppEffect] =
    s.Apply.monixTask[AppStack]

  final val Rates = p.Rates[AppEffect]

}
