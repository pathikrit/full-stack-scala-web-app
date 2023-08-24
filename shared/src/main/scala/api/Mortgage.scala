package api

import framework.RPC
import upickle.default._

case class Mortgage(amount: Double, apr: Double, years: Int)
object Mortgage {
  implicit val rw: ReadWriter[Mortgage] = macroRW
  // TODO: Validator here

  object API {
    val payments = new RPC[Mortgage, Seq[Payment]]("/mortgage/payments")
    val refinancePenalty  = new RPC[(Mortgage, Double), Double]("/mortgage/refinance")
  }
}

case class Payment(principal: Double, interest: Double, balance: Double) {
  val payment = principal + interest
}
object Payment {
  implicit val rw: ReadWriter[Payment] = macroRW
}
