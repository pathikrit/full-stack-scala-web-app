package server

import framework.RPC

@annotation.nowarn
object Router extends cask.MainRoutes {
  @cask.get("/")
  def index() =
    views.App.renderDoc

  @cask.staticFiles("/js/")
  def scalaJs() =
    "web/target/scala-2.13/web-fastopt/"

  @cask.post("/api", subpath = true)
  def routeApi(req: cask.Request) =
    RPC.wire(
      api.MathApi.add ~~> Function.tupled(services.MathApiImpl.add),
      api.MathApi.subtract ~~> Function.tupled(services.MathApiImpl.subtract),
    )(req)

  initialize()
}
