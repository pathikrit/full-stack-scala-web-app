package views

import java.time.{Month, YearMonth}
import scalatags.Text.all._

import scalajs.js.annotation._
import io.udash.wrappers.jquery.{jQ => $}
import squants.Dimensionless
import squants.DimensionlessConversions._
import squants.market._
import squants.market.MoneyConversions._

import scala.annotation.tailrec

import framework.JsRead, JsRead.Dsl

@JSExportTopLevel("annuity")
object AnnuityCalculator extends App(title = "Annuity Calculator", id = "annuity") {
  override def inputs = List(
    row(
      inputRange(
        label = "Deposit",
        id = "deposit",
        value = 3000000,
        min = 0,
        max = 10000000,
        dataStep = 100000,
        tickStep = 1000000,
        prefix = "$",
      ),
    ),
    row(
      inputRange(
        label = "Monthly Withdrawal",
        id = "monthlyWithdrawal",
        value = 20000,
        min = 0,
        max = 50000,
        dataStep = 1000,
        tickStep = 10000,
        prefix = "$",
      ),
    ),
    row(
      inputRange(
        label = "Investment Return Rate",
        id = "irr",
        value = 5,
        min = 0,
        max = 20,
        dataStep = 0.1,
        tickStep = 5,
        suffix = "%",
      ),
    ),
    row(
      inputRange(
        label = "Annual Withdrawal Increase",
        id = "inflation",
        value = 3,
        min = 0,
        max = 5,
        dataStep = 0.1,
        tickStep = 1,
        suffix = "%",
      ),
    ),
    row(
      inputRange( // TODO: Don't show progress bar
        label = "Start",
        id = "year",
        value = 2023, // TODO: Dynamic years
        min = 2022,
        max = 2030,
        dataStep = 1,
        tickStep = 1,
        numberFormat = false,
      ),
    ),
  )

  override def onChange() = {
    val rows = for {
      deposit           <- $("#deposit").value().as[Int].toList
      monthlyWithdrawal <- $("#monthlyWithdrawal").value().as[Int].toList
      irr               <- $("#irr").value().as[Double].toList
      inflation         <- $("#inflation").value().as[Double].toList
      year              <- $("#year").value().as[Int].toList
      (month, withdraw, balance) <- annuity(
        deposit = USD(deposit),
        monthlyWithdrawal = USD(monthlyWithdrawal),
        investmentReturnRate = irr.percent,
        annualWithdrawalIncreaseRate = inflation.percent,
        time = YearMonth.of(year, Month.JANUARY),
        acc = Nil,
      )
    } yield tr(
      th(attr("scope") := "row", month.toString),
      td(withdraw.toFormattedString),
      td(balance.toFormattedString),
    )

    table(style("table", "table-striped", "table-hover"))(
      thead(List("Date", "Withdraw", "Balance").map(th(attr("scope") := "col")(_))),
      tbody(rows),
    )
  }

  @tailrec
  def annuity(
      deposit: Money,
      monthlyWithdrawal: Money,
      investmentReturnRate: Dimensionless,
      annualWithdrawalIncreaseRate: Dimensionless,
      time: YearMonth,
      acc: List[(YearMonth, Money, Money)],
  ): List[(YearMonth, Money, Money)] =
    if (deposit <= 0.dollars || acc.sizeCompare(600) > 0) {
      acc
    } else {
      def resetEoy(x: Dimensionless): Double = if (time.getMonth == Month.DECEMBER) x else 0
      val balance                            = deposit - monthlyWithdrawal
      annuity(
        deposit = balance * (resetEoy(investmentReturnRate) + 1),
        monthlyWithdrawal = monthlyWithdrawal * (resetEoy(annualWithdrawalIncreaseRate) + 1),
        investmentReturnRate = investmentReturnRate,
        annualWithdrawalIncreaseRate = annualWithdrawalIncreaseRate,
        time = time.plusMonths(1),
        acc = (time, monthlyWithdrawal min deposit, balance max 0.dollars) :: acc,
      )
    }
}
