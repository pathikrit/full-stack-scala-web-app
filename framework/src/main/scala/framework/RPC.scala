package framework

import org.scalajs.dom.ext.Ajax
import sttp.model.StatusCode
import upickle.default._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class RPC[I: ReadWriter, O: ReadWriter](path: String) { self =>
  val fragments = path.stripPrefix("/").stripSuffix("/").split("/").toSeq

  def apply(input: I)(implicit ec: ExecutionContext): Future[O] =
    inputValidator(input) match {
      case Left(violations) => Future.failed(new IllegalArgumentException(violations.mkString("\n")))
      case _ =>
        Ajax
          .post(
            url = fragments.mkString(start = "/api/", sep = "/", end = ""),
            data = write(input),
            headers = Map("Content-Type" -> "application/json"),
          )
          .map(res => read[O](res.responseText))
    }

  def ~~>(f: Function[I, O]): RPC.RequestHandler = { req =>
    req.remainingPathSegments match {
      case `fragments` =>
        Try(read[I](req.data)) match {
          case Failure(exception) => RPC.error(StatusCode.BadRequest, errors = List(exception.getMessage))
          case Success(input) =>
            inputValidator(input) match {
              case Left(violations) => RPC.error(StatusCode.BadRequest, errors = violations)
              case _ =>
                Try(f(input)) match {
                  case Failure(exception) => RPC.error(StatusCode.InternalServerError, errors = List(exception.getMessage))
                  case Success(result)    => cask.Response(data = writeJs(result))
                }
            }
        }
    }
  }

  def inputValidator: Validation[I] = Validation.empty
}

object RPC {
  type RequestHandler = PartialFunction[cask.Request, cask.Response[ujson.Value]]

  def error(statusCode: StatusCode, errors: List[String] = Nil) =
    cask.Response(data = ujson.Obj("errors" -> errors), statusCode = statusCode.code)

  def wire(handlers: RequestHandler*): cask.Request => cask.Response[ujson.Value] =
    handlers.reduce(_ orElse _).orElse(_ => error(StatusCode.NotFound))
}
