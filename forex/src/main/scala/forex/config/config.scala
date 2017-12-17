package forex.config

import scala.concurrent.duration.FiniteDuration

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.string.Url
import org.zalando.grafter.macros._

@readers
case class ApplicationConfig(
    akka: AkkaConfig,
    api: ApiConfig,
    executors: ExecutorsConfig,
    forge: ForgeConfig,
    cache: CacheConfig
)

case class AkkaConfig(
    name: String,
    exitJvmTimeout: Option[FiniteDuration]
)

case class ApiConfig(
    interface: String,
    port: Int
)

case class ExecutorsConfig(
    default: String
)

case class CacheConfig(maximumSize: Int Refined Positive, timeToLive: FiniteDuration)

case class ForgeConfig(key: String,
                       url: String Refined Url,
                       readTimeOut: Option[FiniteDuration],
                       connectionTimeOut: Option[FiniteDuration])
