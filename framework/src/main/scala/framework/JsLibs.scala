package framework

import scalatags.Text.all._

object JsLibs {
  def css(lib: String, version: String): Tag =
    link(href := s"https://cdn.jsdelivr.net/npm/$lib@$version/dist/css/$lib.min.css", rel := "stylesheet")

  def js(lib: String, version: String): Tag =
    js(lib = lib, version = version, file = lib)

  def js(lib: String, version: String, file: String): Tag =
    script(src := s"https://cdn.jsdelivr.net/npm/$lib@$version/dist/$file.min.js")

  val jquery = js("jquery", version = "3.2.1")

  object bootstrap {
    val version = "5.1.3"
    val css     = JsLibs.css("bootstrap", version = version)
    val js      = JsLibs.js("bootstrap", version = version, file = "js/bootstrap.bundle")
  }
}
