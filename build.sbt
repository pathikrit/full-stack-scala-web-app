Global / onChangedBuildSource := ReloadOnSourceChanges

def module(name: String) = Project(id = name, base = file(name))
  .settings(scalaVersion := "2.13.7")

lazy val framework = module("framework")
  .enablePlugins(ScalaJSPlugin)
  .settings(libraryDependencies ++= Seq(Dependencies.cask) ++ Dependencies.scalaJs.value)

lazy val shared = module("shared")
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(framework)

lazy val web = module("web")
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(shared)

lazy val server = module("server")
  .dependsOn(web)
  .settings(reStart / baseDirectory := file("."))
  .settings(libraryDependencies ++= Seq(
    Dependencies.requests,
    Dependencies.logback,
    Dependencies.scalaLogging,
  ))

lazy val root = project.in(file("."))
  .aggregate(server, framework, shared, web)

def cmd(name: String, commands: String*) =
  Command.command(name)(s => s.copy(remainingCommands = commands.toList.map(cmd => Exec(cmd, None)) ++ s.remainingCommands))

commands ++= List(
  cmd("dev", "scalafmtAll", "~;web/fastOptJS;server/reStart;", "server/reStop")
)
