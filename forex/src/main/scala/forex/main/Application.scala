package forex.main

import forex.config._
import org.zalando.grafter.macros._
import org.zalando.grafter.syntax.rewriter._

@readerOf[ApplicationConfig]
final case class Application(
    api: Api
) {
  def configure(): Application = this.singletons
}
