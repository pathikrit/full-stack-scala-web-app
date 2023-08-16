package views

//import io.udash.wrappers.jquery.{jQ => $}
import scalatags.Text.{all => t}
import scalatags.Text.all._

object MortgageCalculator extends framework.Page("mortgage_calculator") {
  override def renderBody = body(
    div(`class` := "container")(
      h3("Mortgage Calculator"),
      form(t.id := "calculator")(
        input("Loan Amount", prefix = "$"),
        input("APR", suffix = "%"),
        input("Mortgage Period", suffix = "years"),
      ),
    ),
  )

  def input(label: String, prefix: String = "", suffix: String = "") = {
    val id = label.replaceAll("\\s", "_").toLowerCase
    div(`class` := "mb-3")(
      t.label(label, `for` := id, `class` := "col-form-label"),
      ": " + prefix,
      t.input(t.id := id, `type` := "number"),
      suffix
    )
  }
}
