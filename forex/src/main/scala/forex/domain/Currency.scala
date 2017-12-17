package forex.domain

import cats.Show
import io.circe._

sealed trait Currency
object Currency {
  final case object AUD extends Currency
  final case object CAD extends Currency
  final case object CHF extends Currency
  final case object EUR extends Currency
  final case object GBP extends Currency
  final case object NZD extends Currency
  final case object JPY extends Currency
  final case object SGD extends Currency
  final case object USD extends Currency
  final case object NOT_SUPPORT extends Currency

  implicit val show: Show[Currency] = Show.show {
    case AUD         ⇒ "AUD"
    case CAD         ⇒ "CAD"
    case CHF         ⇒ "CHF"
    case EUR         ⇒ "EUR"
    case GBP         ⇒ "GBP"
    case NZD         ⇒ "NZD"
    case JPY         ⇒ "JPY"
    case SGD         ⇒ "SGD"
    case USD         ⇒ "USD"
    case NOT_SUPPORT ⇒ "NOT SUPPORT"
  }

  def fromString(s: String): Currency = s match {
    case "AUD" | "aud" ⇒ AUD
    case "CAD" | "cad" ⇒ CAD
    case "CHF" | "chf" ⇒ CHF
    case "EUR" | "eur" ⇒ EUR
    case "GBP" | "gbp" ⇒ GBP
    case "NZD" | "nzd" ⇒ NZD
    case "JPY" | "jpy" ⇒ JPY
    case "SGD" | "sgd" ⇒ SGD
    case "USD" | "usd" ⇒ USD
    case _             ⇒ NOT_SUPPORT
  }

  implicit val encoder: Encoder[Currency] =
    Encoder.instance[Currency] { show.show _ andThen Json.fromString }

  implicit val decoder: Decoder[Currency] =
    Decoder.instance[Currency] { c ⇒
      c.as[String].fold(_ ⇒ Right(NOT_SUPPORT), fb ⇒ Right(fromString(fb)))
    }
}
