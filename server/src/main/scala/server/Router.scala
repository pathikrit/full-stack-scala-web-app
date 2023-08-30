package server

import framework.RPC

@annotation.nowarn
object Router extends cask.MainRoutes {
  val isProd = sys.env.get("IS_PROD").flatMap(_.toBooleanOption).exists(identity)

  @cask.get("/")
  def index() = views.MortgageCalculator.renderDoc

  @cask.staticFiles("/js/")
  def scalaJs() = s"web/target/scala-2.13/${if (isProd) "web-opt" else "web-fastopt"}/"

  @cask.post("/api", subpath = true)
  def routeApi(req: cask.Request) =
    RPC.wire(
      api.Mortgage.API.payments ~~> services.MortgageApiImpl.payments,
      api.Mortgage.API.refinancePenalty ~~> Function.tupled(services.MortgageApiImpl.refinancePenalty),
    )(req)

  initialize()
}
