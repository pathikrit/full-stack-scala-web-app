package api

import framework.RPC

object MathApi {
  val add      = new RPC[(Int, Int), Int]("/math/add")
  val subtract = new RPC[(Int, Int), Int]("/math/subtract")
}
