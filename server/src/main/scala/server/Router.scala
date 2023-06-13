package server

import api.MathApi
import framework.RPC

@annotation.nowarn // See https://github.com/com-lihaoyi/cask/issues/62
object Router extends cask.MainRoutes {
  @cask.get("/")
  def index() = // Redirect to first app
    cask.Redirect(s"/app/${views.App.registry.head.route}")

  @cask.get(s"/app/:app")
  def routeApp(app: String) =
    views.App.route(app)

  @cask.staticFiles("/js/")
  def scalaJs() =
    "web/target/scala-2.13/web-fastopt/"

  val apiRouter = RPC.wire(
    MathApi.add ~~> Function.tupled(MathApiImpl.add),
    MathApi.subtract ~~> Function.tupled(MathApiImpl.subtract),
  )

  @cask.post("/api", subpath = true)
  def routeApi(req: cask.Request) =
    apiRouter(req)

  initialize()
}

object MathApiImpl {
  def add(x: Int, y: Int) = {
    println(s"add($x, $y)")
    x + y
  }

  def subtract(x: Int, y: Int) = {
    println(s"subtract($x, $y)")
    x - y
  }
}
