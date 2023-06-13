package framework

import io.udash.wrappers.jquery._

import scalajs.js.annotation.JSExport
import scalatags.Text.all._
import scalatags.Text.TypedTag
import scalacss.DevDefaults._
import scalacss.ScalatagsCss._

import scala.collection.mutable

abstract class Page(name: String) {
  implicit val ec = scala.concurrent.ExecutionContext.global

  final lazy val renderDoc: doctype = doctype("html")(html(renderHead, renderBody, scripts))

  def styles: List[StyleSheet.Standalone] = {
    import scala.language.postfixOps
    List(
      new StyleSheet.Standalone {
        import dsl._
        List(".slider", ".slider-horizontal").mkString - (
          width(100 %%),
        )
      },
    )
  }

  def cssLibs: List[Modifier] = List(
    JsLibs.bootstrap.css,
    JsLibs.bootstrap_slider.css,
    styles.map(_.render[TypedTag[String]]),
  )

  def jsLibs: List[Modifier] = List(
    JsLibs.jquery,
    JsLibs.bootstrap.js,
    JsLibs.bootstrap_slider.js,
  )

  def kv(kvs: (String, Any)*): String = kvs.map({ case (k, v) => s"$k=$v" }).mkString(", ")

  def renderHead: Modifier = head(
    meta(charset := "utf-8"),
    meta(attr("name") := "viewport", content := kv("width" -> "device-width", "initial-scale" -> "1")), // required by Bootstrap
    cssLibs,
  )

  def renderBody: Modifier

  private val initBuffer  = mutable.Buffer.empty[() => Any]
  private val initScripts = mutable.Buffer(s"$name.init()")

  def scripts: List[Modifier] =
    jsLibs ++ List(script(src := "/js/main.js"), script((initScripts.mkString(";\n"))))

  def addScript(js: String): Unit =
    initScripts += js.trim

  def registerHandler(id: String, on: EventName, onLoad: Boolean)(callback: () => Any): Unit = {
    initBuffer += { () => jQ(s"#$id").on(event = on, callback = (_, _) => callback()) }
    if (onLoad) initBuffer += { () => jQ(callback) }
  }

  @JSExport
  final def init(): Unit = {
    require(renderDoc.toString.nonEmpty) // This exists to trigger initBuffer population before compilation to JS
    initBuffer.foreach(_.apply())
  }
}
