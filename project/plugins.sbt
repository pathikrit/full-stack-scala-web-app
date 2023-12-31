addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.7.1")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.3")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")
addSbtPlugin("org.jmotor.sbt" % "sbt-dependency-updates" % "1.2.2")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.20")
libraryDependencies += "org.scala-js" %% "scalajs-env-nodejs" % "1.2.1" // https://github.com/scala-js/scala-js-js-envs/issues/12
