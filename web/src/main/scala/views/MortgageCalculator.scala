package views

import scalatags.Text.all._

import scala.annotation.nowarn
import scalajs.js.annotation.JSExportTopLevel
import org.scalajs.dom.raw._
import scalatags.Text.{all => t}

import io.udash.wrappers.jquery.{jQ => $, _}

@JSExportTopLevel("mortgage_calculator")
object MortgageCalculator extends framework.Page("mortgage_calculator") {
  override def renderBody = body(
    div(`class` := "container card w-25 mt-5 p-3")(
      h3("Mortgage Calculator"),
      t.form(t.id := "calculator")(
        input("Loan Amount ($)", id = "loan", default = 1e6.toInt),
        input("APR (%)", id = "apr", default = 5),
        input("Mortgage Period (years)", id = "years", default = 30),
        button("Calculate", id := "calc_payments", `type` := "button", `class` := "btn btn-primary"),
        tag("output")(id       := "output"),
      ),
    ),
  )

  def input(label: String, id: String, default: Int): Tag =
    div(`class`            := "mb-3")(
      t.label(label, `for` := id, `class`   := "col-form-label"),
      t.input(value        := default, t.id := id, `type` := "number", `class` := "form-control"),
    )

  override def init() = {
    $("#calc_payments").on("click", calc)
  }

  @nowarn
  def calc(element: Element, event: JQueryEvent) = {
    import api.Mortgage
    for {
      amount <- $("#loan").value().asInstanceOf[String].toIntOption
      apr    <- $("#apr").value().asInstanceOf[String].toFloatOption
      years  <- $("#years").value().asInstanceOf[String].toIntOption
      mortgage = Mortgage(amount = amount, apr = apr, years = years)
      res <- Mortgage.API.schedule(mortgage)
    } $("#output").text(res.mkString(","))
  }
}
