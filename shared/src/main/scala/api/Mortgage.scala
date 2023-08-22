package api

import framework.RPC
import upickle.default._

case class Mortgage(amount: Double, apr: Double, years: Int)
object Mortgage {
  implicit val rw: ReadWriter[Mortgage] = macroRW
  // TODO: Validator here

  object API {
    val schedule = new RPC[Mortgage, Seq[Payment]]("/mortgage/schedule")
    val combine  = new RPC[(Mortgage, Mortgage), Mortgage]("/mortgage/combine")
  }
}

case class Payment(principal: Double, interest: Double, balance: Double)
object Payment {
  implicit val rw: ReadWriter[Payment] = macroRW
}
