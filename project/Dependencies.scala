import sbt._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {
  val cask = "com.lihaoyi" %% "cask" % "0.8.0"

  val requests = "com.lihaoyi" %% "requests" % "0.7.1"

  val logback = "ch.qos.logback" % "logback-classic" % "1.2.10"
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"

  val scalaJs = Def.setting(Seq( // see https://github.com/vmunier/play-scalajs.g8/issues/20
    // Tests
    "org.scalameta" %%% "munit" % "0.7.29" % Test,

    // Scala JS
    "org.scala-js" %%% "scalajs-dom" % "1.1.0",
    "io.github.cquiroz" %%% "scala-java-time" % "2.2.2",
    "com.lihaoyi" %%% "scalatags" % "0.9.4",

    // Scala CSS
    "com.github.japgolly.scalacss" %%% "core" % "0.8.0-RC1",
    "com.github.japgolly.scalacss" %%% "ext-scalatags" % "0.8.0-RC1",

    // JS Wrappers
    "io.udash" %%% "udash-jquery" % "3.0.4",
    "org.openmole.scaladget" %%% "bootstrapslider" % "1.3.7",

    // API Layer
    "com.lihaoyi" %%% "upickle" % "1.3.8",
    "com.softwaremill.sttp.model" %%% "core" % "1.3.4",

    // Service Layer
    "org.typelevel" %%% "squants"  % "1.6.0"
  ))
}
