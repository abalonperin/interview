package forex.processes.rates

import forex.domain.Currency

final case class GetRequest(
    from: Currency,
    to: Currency
)
