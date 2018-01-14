package forex.interfaces.api.rates

import javax.ws.rs.Path

import akka.http.scaladsl._
import forex.config._
import forex.domain.Currency
import forex.interfaces.api.rates.Protocol.GetApiResponse
import forex.interfaces.api.utils._
import forex.main._
import io.swagger.annotations.{
  ApiImplicitParam,
  ApiImplicitParams,
  ApiOperation,
  ApiResponse,
  ApiResponses,
  Api ⇒ SwaggerAPi
}
import org.zalando.grafter.macros._

@readerOf[ApplicationConfig]
@SwaggerAPi(value = "/", produces = "application/json")
@Path("/")
final case class Routes(
    processes: Processes,
    runners: Runners
) {
  import ApiMarshallers._
  import Converters._
  import Directives._
  import processes._
  import runners._
  import server.Directives._

  @Path("?from={from}&to={to}")
  @ApiOperation(value = "Get exchange rate", httpMethod = "GET", response = classOf[GetApiResponse])
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "from",
        value = "Currency, for example: USD",
        required = true,
        dataTypeClass = classOf[Currency],
        paramType = "path"
      ),
      new ApiImplicitParam(
        name = "to",
        value = "Currency, for example: JPY",
        required = true,
        dataTypeClass = classOf[Currency],
        paramType = "path"
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "Return exchange rate", response = classOf[GetApiResponse]),
      new ApiResponse(code = 400, message = "Bad request"),
      new ApiResponse(code = 500, message = "Internal server error")
    )
  )
  def route: server.Route =
    get {
      getApiRequest { req ⇒
        complete {
          runApp(
            Rates
              .get(toGetRequest(req))
              .map(_.map(result ⇒ toGetApiResponse(result)))
          )
        }
      }
    }

}
