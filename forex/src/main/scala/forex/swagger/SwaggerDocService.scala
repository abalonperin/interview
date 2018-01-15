package forex.swagger

import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.Info
import forex.config.{ ApiConfig, ApplicationConfig }
import forex.interfaces.api.rates.Routes
import org.zalando.grafter.macros.readerOf

@readerOf[ApplicationConfig]
final case class SwaggerDocService(apiConfig: ApiConfig) {
  def service = new SwaggerHttpService {
    override val apiClasses = Set(classOf[Routes])
    override val host = s"${apiConfig.interface}:${apiConfig.port}"
    override val info = Info(version = "1.0")
  }
}
