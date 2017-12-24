package forex

import forex.processes.rates.Processes

package object processes {
  type Rates[F[_]] = rates.Processes[F]
  val Rates: Processes.type = rates.Processes

  type RatesError = ProcessError
  val RatesError: ProcessError.type = processes.ProcessError

}
