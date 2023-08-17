package framework

//import io.udash.wrappers.jquery._

import scalajs.js.annotation.JSExport
import scalatags.Text.all._
//import scalatags.Text.TypedTag
import scalacss.DevDefaults._
//import scalacss.ScalatagsCss._


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

  def cssLibs: List[Tag] = List(
    JsLibs.bootstrap.css,
    //styles.map(_.render[TypedTag[String]]),
  )

  def jsLibs: List[Tag] = List(
    JsLibs.jquery,
    JsLibs.bootstrap.js,
  )

  def scripts: List[Modifier] = jsLibs ++ List(
    script(src := "/js/main.js"),
    script(s"$name.init()"),
  )

  def kv(kvs: (String, Any)*): String = kvs.map({ case (k, v) => s"$k=$v" }).mkString(", ")

  def renderHead: Tag = head(
    meta(charset := "utf-8"),
    meta(attr("name") := "viewport", content := kv("width" -> "device-width", "initial-scale" -> "1")), // required by Bootstrap
    cssLibs,
  )

  def renderBody: Tag

  @JSExport
  def init(): Unit
}
