package forex.main

import java.nio.ByteBuffer

import com.softwaremill.sttp.asynchttpclient.monix.AsyncHttpClientMonixBackend
import com.softwaremill.sttp.{ SttpBackend, SttpBackendOptions }
import forex.config._
import forex.processes.rates
import forex.{ processes ⇒ p, services ⇒ s }
import monix.eval.Task
import monix.reactive.Observable
import org.zalando.grafter.macros._

@readerOf[ApplicationConfig]
final case class Processes(forgeConfig: ForgeConfig, cacheConfig: CacheConfig) {

  implicit val sttpBackend: SttpBackend[Task, Observable[ByteBuffer]] =
    forgeConfig.connectionTimeOut.fold(AsyncHttpClientMonixBackend())(
      f ⇒ AsyncHttpClientMonixBackend(SttpBackendOptions.connectionTimeout(f))
    )

  implicit lazy val _oneForge: s.OneForge[AppEffect] =
    s.OneForge.live[AppStack](forgeConfig)

  implicit lazy val _cache: s.Cache[AppEffect] =
    s.Cache.caffeineCache[AppStack](cacheConfig)

  implicit lazy val _apply: s.Apply[AppEffect] =
    s.Apply.monixTask[AppStack]

  val Rates: rates.Processes[AppEffect] = p.Rates[AppEffect]

}
