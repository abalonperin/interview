package forex.config

import scala.concurrent.duration.FiniteDuration

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.string.Url
import org.zalando.grafter.macros._

@readers
final case class ApplicationConfig(
    akka: AkkaConfig,
    api: ApiConfig,
    executors: ExecutorsConfig,
    forge: ForgeConfig,
    cache: CacheConfig
)

final case class AkkaConfig(
    name: String,
    exitJvmTimeout: Option[FiniteDuration]
)

final case class ApiConfig(
    interface: String,
    port: Int
)

final case class ExecutorsConfig(
    default: String
)

final case class CacheConfig(maximumSize: Int Refined Positive, timeToLive: FiniteDuration)

final case class ForgeConfig(key: String,
                             url: String Refined Url,
                             readTimeOut: Option[FiniteDuration],
                             connectionTimeOut: Option[FiniteDuration])
