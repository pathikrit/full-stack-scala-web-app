package services

import api.Mortgage

object MortgageApiImpl {
  def payments(m: Mortgage): List[Double] = {
    println(s"payments($m)")
    List(m.amount.toDouble, m.apr, m.years.toDouble)
  }

  def combine(a: Mortgage, b: Mortgage): Mortgage = {
    println(s"combine($a, $b)")
    a
  }
}
