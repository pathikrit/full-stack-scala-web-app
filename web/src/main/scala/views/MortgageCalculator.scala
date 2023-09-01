package views

import scala.annotation.nowarn
import scalajs.js.annotation.JSExportTopLevel
import org.scalajs.dom.raw._
import scalatags.Text.{all => t}
import scalatags.Text.all._
import io.udash.wrappers.jquery.{jQ => $, _}

import java.text.DecimalFormat

import framework.JsRead._
import api.Mortgage

@JSExportTopLevel("mortgage_calculator")
object MortgageCalculator extends framework.Page("mortgage_calculator") {
  override def renderBody = body(
    div(`class` := "container card w-25 mt-5 p-3")(
      h3("Mortgage Calculator"),
      form(t.id := "calculator")(
        input(label = "Loan Amount ($)", id = "loan", default = 1e6.toInt),
        input(label = "APR (%)", id = "apr", default = 5),
        input(label = "Mortgage Period (years)", id = "years", default = 30),
        input(label = "New APR", id = "new_apr", default = 3),
        button("Calculate", id  := "calc_payments", `type` := "button", `class` := "btn btn-primary m-2"),
        button("Refinance?", id := "refinance", `type`     := "button", `class` := "btn btn-secondary m-2"),
      ),
    ),
    div(id := "output", `class` := "container"),
  )

  def input(label: String, id: String, default: Int): Tag =
    div(`class`            := "mb-3")(
      t.label(label, `for` := id, `class`   := "col-form-label"),
      t.input(value        := default, t.id := id, `type` := "number", `class` := "form-control"),
    )

  override def init() = {
    $("#calc_payments").on("click", calculate_payments)
    $("#refinance").on("click", calculate_refinance)
  }

  val moneyFormat = new DecimalFormat("$ #.00")

  @nowarn
  def calculate_payments(element: Element, event: JQueryEvent) = {
    $("#output").html(
      table(`class` := "table table-striped font-monospace")(
        tr(th("#"), th("Balance"), th("Payment"), th("Principal"), th("Interest")),
      ).render,
    )
    for {
      amount <- $("#loan").value().as[Int]
      apr    <- $("#apr").value().as[Double]
      years  <- $("#years").value().as[Int]
      mortgage = Mortgage(amount = amount, apr = apr, years = years)
      payments       <- Mortgage.API.payments(mortgage)
      (payment, row) <- payments.zipWithIndex
    } $("#output tr:last").after(
      tr(
        td(row + 1),
        td(moneyFormat.format(payment.balance)),
        td(moneyFormat.format(payment.payment)),
        td(moneyFormat.format(payment.principal)),
        td(moneyFormat.format(payment.interest)),
      ).render,
    )
  }

  @nowarn
  def calculate_refinance(element: Element, event: JQueryEvent) = {
    for {
      amount <- $("#loan").value().as[Int]
      apr    <- $("#apr").value().as[Double]
      years  <- $("#years").value().as[Int]
      newApr <- $("#new_apr").value().as[Double]
      mortgage = Mortgage(amount = amount, apr = apr, years = years)
      penalty <- Mortgage.API.refinancePenalty(mortgage, newApr)
    } $("#output").html(
      div(`class` := "container")(s"You will save ${moneyFormat.format(penalty)} over $years years in interest").render,
    )
  }
}
