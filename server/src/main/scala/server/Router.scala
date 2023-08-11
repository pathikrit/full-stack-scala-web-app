package server

import framework.RPC

@annotation.nowarn
object Router extends cask.MainRoutes {
  @cask.get("/")
  def index() = views.App.renderDoc

  @cask.staticFiles("/js/")
  def scalaJs() = "web/target/scala-2.13/web-fastopt/"

  @cask.post("/api", subpath = true)
  def routeApi(req: cask.Request) =
    RPC.wire(
      api.Mortgage.API.monthlyPayments ~~> services.MortgageApiImpl.payments,
      api.Mortgage.API.combine ~~> Function.tupled(services.MortgageApiImpl.combine),
    )(req)

  initialize()
}
