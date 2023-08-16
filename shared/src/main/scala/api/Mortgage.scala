package api

import framework.RPC

case class Mortgage(principal: Double, apr: Double, years: Int)
object Mortgage {
  import upickle.default._
  implicit val rw: ReadWriter[Mortgage] = macroRW

  object API {
    val monthlyPayments = new RPC[Mortgage, List[Double]]("/mortgage/payments")
    val combine         = new RPC[(Mortgage, Mortgage), Mortgage]("/mortgage/combine")
  }
}

// TODO: Validator here
