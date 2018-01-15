package forex.interfaces.api

import akka.http.scaladsl._
import forex.config._
import forex.swagger.{ SwaggerDocService, SwaggerUiService }
import org.zalando.grafter.macros._
import utils._

@readerOf[ApplicationConfig]
final case class Routes(
    ratesRoutes: rates.Routes,
    swaggerDocService: SwaggerDocService
) {
  import server.Directives._

  lazy val route: server.Route =
    handleExceptions(ApiExceptionHandler()) {
      handleRejections(ApiRejectionHandler()) {
        ratesRoutes.route ~ swaggerDocService.service.routes ~ SwaggerUiService.swaggerSiteRoute
      }
    }
}
