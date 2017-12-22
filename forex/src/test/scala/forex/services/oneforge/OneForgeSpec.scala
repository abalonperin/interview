package forex.services.oneforge

import java.nio.ByteBuffer
import java.util.concurrent.TimeoutException

import scala.concurrent.Await
import scala.concurrent.duration._

import cats.data.EitherT
import cats.implicits._
import com.softwaremill.sttp.asynchttpclient.monix.AsyncHttpClientMonixBackend
import com.softwaremill.sttp.testing.SttpBackendStub
import com.softwaremill.sttp.{ Method, SttpBackendOptions }
import eu.timepit.refined.auto._
import forex.config.ForgeConfig
import forex.domain.{ Currency, Price, Rate, Timestamp }
import forex.services.ServiceError.ForgeError
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import monix.reactive.Observable
import org.atnos.eff.syntax.addon.monix.task._
import org.scalatest.{ AsyncFlatSpec, Matchers }
import io.circe.syntax._

class OneForgeSpec extends AsyncFlatSpec with Matchers {

  "One Forge" should "convert from one currency to another successfully" in {
    val config =
      ForgeConfig("api key", "http://test.com", None, None)

    implicit val testingBackend =
      SttpBackendStub[Task, Observable[ByteBuffer], Observable[ByteBuffer]](AsyncHttpClientMonixBackend())
        .whenRequestMatches(
          p ⇒
            p.uri.paramsSeq.equals(
              List(
                ("from", Currency.USD.asInstanceOf[Currency].show),
                ("to", Currency.EUR.asInstanceOf[Currency].show),
                ("quantity", "1"),
                ("api_key", "api key")
              )
            ) && p.method == Method.GET
        )
        .thenRespond("""{"value":0.842162,"text":"1 USD is worth 0.842162 EUR","timestamp":1513840116}""")

    val result = for {
      r ← EitherT(
        Interpreters
          .live(config)
          .get(Rate.Pair(Currency.USD, Currency.EUR))
      )
    } yield { r }

    result.value.runAsync.runAsync
      .map(
        _.fold(
          _ ⇒ assert(false),
          fb ⇒ {
            fb.pair shouldBe Rate.Pair(Currency.USD, Currency.EUR)
            fb.price.value shouldBe BigDecimal(0.842162)
            fb.timestamp.value.toEpochSecond shouldBe 1513840116
          }
        )
      )

  }

  it should "get an error when using a illegal api key" in {
    val config = ForgeConfig("illegal api key", "http://test.com", None, None)

    val errorMessage =
      "API Key Not Valid. Please go to 1forge.com to get an API key. If you have any questions please email us at contact@1forge.com"

    implicit val testingBackend =
      SttpBackendStub[Task, Observable[ByteBuffer], Observable[ByteBuffer]](AsyncHttpClientMonixBackend())
        .whenRequestMatches(
          p ⇒
            p.uri.paramsSeq.equals(
              List(
                ("from", Currency.USD.asInstanceOf[Currency].show),
                ("to", Currency.JPY.asInstanceOf[Currency].show),
                ("quantity", "1"),
                ("api_key", "illegal api key")
              )
            ) && p.method == Method.GET
        )
        .thenRespond(s"""{"error":true,"message":"$errorMessage"}""")

    val result = for {
      r ← EitherT(
        Interpreters
          .live(config)
          .get(Rate.Pair(Currency.USD, Currency.JPY))
      )
    } yield { r }

    result.value.runAsync.runAsync
      .map(
        _.fold(
          fa ⇒ {
            fa shouldBe a[ForgeError]
            fa.asInstanceOf[ForgeError].reason should not be ""
            fa.asInstanceOf[ForgeError].statusCode shouldBe 200
            fa.asInstanceOf[ForgeError].reason shouldBe errorMessage
            fa.asInstanceOf[ForgeError].throwable shouldBe None
          },
          _ ⇒ assert(false)
        )
      )
  }

  it should "get an error when connect timeout" in {
    val config = ForgeConfig("api key", "http://test.com", None, 1.seconds.some)

    implicit val testingBackend =
      SttpBackendStub[Task, Observable[ByteBuffer], Observable[ByteBuffer]](
        AsyncHttpClientMonixBackend(SttpBackendOptions.connectionTimeout(config.connectionTimeOut.get))
      ).whenRequestMatches(
          p ⇒
            p.uri.paramsSeq.equals(
              List(
                ("from", Currency.USD.asInstanceOf[Currency].show),
                ("to", Currency.JPY.asInstanceOf[Currency].show),
                ("quantity", "1"),
                ("api_key", "api key")
              )
            ) && p.method == Method.GET
        )
        .thenRespond(throw new TimeoutException())

    val result = for {
      r ← EitherT(
        Interpreters
          .live(config)
          .get(Rate.Pair(Currency.USD, Currency.JPY))
      )
    } yield { r }

    recoverToSucceededIf[TimeoutException] {
      result.value.runAsync.runAsync
    }
  }

  it should "get an error when read timeout" in {
    Await
    val config = ForgeConfig("api key", "http://test.com", 1.seconds.some, None)

    implicit val testingBackend =
      SttpBackendStub[Task, Observable[ByteBuffer], Observable[ByteBuffer]](AsyncHttpClientMonixBackend())
        .whenRequestMatches(
          p ⇒
            p.uri.paramsSeq.equals(
              List(
                ("from", Currency.USD.asInstanceOf[Currency].show),
                ("to", Currency.JPY.asInstanceOf[Currency].show),
                ("quantity", "1"),
                ("api_key", "api key")
              )
            ) && p.method == Method.GET
        )
        .thenRespond(throw new TimeoutException())

    val result = for {
      r ← EitherT(
        Interpreters
          .live(config)
          .get(Rate.Pair(Currency.USD, Currency.JPY))
      )
    } yield { r }

    recoverToSucceededIf[TimeoutException] {
      result.value.runAsync.runAsync
    }
  }
}
