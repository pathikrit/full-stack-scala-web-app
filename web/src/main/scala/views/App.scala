package views

import io.udash.wrappers.jquery.{jQ => $}
//import scaladget.bootstrapslider._
import scalatags.Text.{all => t}
import scalatags.Text.all._
import scalatags.Text.tags2.nav
import scalatags._

import java.text.NumberFormat
import java.util.Locale

object App extends framework.Page("calculator") {
  val id                     = "Calculator"
  val title                  = "Calculator"
  def inputs: List[Modifier] = Nil

  def onChange(): Any = {}

  def route: String = id

  def renderForm: Modifier = {
    registerHandler(id, "change", onLoad = true)({ () =>
      $(s"#${id}_output").html(onChange().toString())
    })
    div(style("container"))(
      row(h3(title), style("mb-3")),
      form(t.id := id, inputs),
      row(div(t.id := s"${id}_output")),
    )
  }

//  def renderNavBar: Modifier =
//    navbar(title)(App.registry.map({ app => app.title -> app.route }): _*)

  override def renderBody =
    body(renderForm)

  def inputRange(
      label: String,
      id: String,
      value: Double,
      min: Double,
      max: Double,
      dataStep: Double,
      tickStep: Double,
      numberFormat: Boolean = true,
      prefix: String = "",
      suffix: String = "",
  ): Modifier = {
    require(min <= value && value <= max, s"Value=$value not in [$min, $max] in $id")

    // TODO: Use bootstrap-slider once https://github.com/openmole/scaladget/pull/12 is fixed
    addScript(s"""
      |$$('#$id').slider({
      |  formatter: function(value) {
      |    return '$prefix' + ${if (numberFormat) "value.toLocaleString()" else "value"} + '$suffix'
      |  }
      |})
      |""".stripMargin)

    def ticks = Iterator.iterate(min)(_ + tickStep).takeWhile(_ <= max)

    div(style("mb-3"))(
      t.label(label, `for` := id, style("form-label")),
      t.input(
        t.id                      := id,
        `type`                    := "text",
        attr("data-slider-min")   := min.toString,
        attr("data-slider-max")   := max.toString,
        attr("data-slider-step")  := dataStep.toString,
        attr("data-slider-value") := value.toString,
        attr("data-slider-ticks") := ticks.mkString("[", ", ", "]"),
        // attr("data-slider-ticks-snap-bounds") := tickStep.toString,
        attr("data-slider-ticks-labels") := ticks
          .map(x => s"\"${prefix + (if (numberFormat) commaFormatNumber(x) else x.toInt) + suffix}\"")
          .mkString("[", ", ", "]"),
        attr("data-slider-tooltip") := "always",
      ),
    )
  }

  def commaFormatNumber(number: Double): String =
    NumberFormat.getNumberInstance(Locale.US).format(number)

  def row(items: Modifier*): Modifier =
    div(style("row"))(items.map(div(style("col"))(_)))

  def navbar(current: String)(items: (String, String)*): Modifier =
    nav(style("navbar", "navbar-expand-lg", "navbar-dark", "bg-dark", "sticky-top"))(
      divs(style("container-fluid"), style("collapse", "navbar-collapse"), style("navbar-nav"))(
        items.map({
          case (`current`, link) => a(current, style("nav-link", "active"), attr("aria-current") := "page", href := link)
          case (name, link)      => a(name, style("nav-link"), href := link)
        }),
      ),
    )

  private type Style = generic.AttrPair[text.Builder, String]

  /** Nest divs */
  def divs(styles: Style*)(modifiers: Modifier*): Modifier = {
    def recurse(_styles: List[Style]): Text.TypedTag[String] = {
      _styles match {
        case Nil       => div(modifiers)
        case s1 :: Nil => div(s1)(modifiers)
        case s1 :: s2  => div(s1)(recurse(s2))
      }
    }
    recurse(styles.toList)
  }

  def style(styles: String*): Style =
    `class` := styles.mkString(" ")
}
