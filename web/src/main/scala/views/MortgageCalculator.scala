package views

//import io.udash.wrappers.jquery.{jQ => $}
import scalatags.Text.{all => t}
import scalatags.Text.all._

object MortgageCalculator extends framework.Page("mortgage_calculator") {
  override def renderBody = body(
    div(`class` := "container")(
      h3("Mortgage Calculator"),
      t.form(t.id := "calculator")(
        input("Loan Amount ($)", default = 1e6.toInt),
        input("APR (%)", default = 5),
        input("Mortgage Period (years)", default = 30),
        button("Calculate", `type` := "submit")
      ),
    ),
  )

  def input(label: String, default: Int): Tag = {
    val id = label.replaceAll("\\s", "_").toLowerCase
    div(`class` := "mb-3 w-25")(
      t.label(label, `for` := id, `class` := "col-form-label"),
      t.input(value := default, t.id := id, `type` := "number", `class` := "form-control"),
    )
  }
}
