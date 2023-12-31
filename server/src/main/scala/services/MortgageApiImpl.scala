package services

import api.{Mortgage, Payment}

object MortgageApiImpl {
  def payments(mortgage: Mortgage): Seq[Payment] = {
    println(s"schedule($mortgage)")
    val r              = mortgage.apr / 100 / 12
    val n              = 12 * mortgage.years
    val c              = (x: Int) => 1 - Math.pow(1 + r, -x.toDouble)
    val monthlyPayment = mortgage.amount * r / c(n)
    val balance        = (i: Int) => mortgage.amount * c(n - i) / c(n)
    val principal      = (i: Int) => balance(i - 1) - balance(i)
    val interest       = (i: Int) => monthlyPayment - principal(i)
    (1 to n).map(i => Payment(principal = principal(i), interest = interest(i), balance = balance(i)))
  }

  def refinancePenalty(mortgage: Mortgage, newApr: Double): Double = {
    println(s"refinance($mortgage, newApr=$newApr)")
    def totalInterest(m: Mortgage) = payments(m).map(_.interest).sum
    totalInterest(mortgage) - totalInterest(mortgage.copy(apr = newApr))
  }
}
