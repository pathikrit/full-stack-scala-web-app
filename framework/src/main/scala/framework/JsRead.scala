package framework

import scala.scalajs.js.|
import scala.util.Try

trait JsRead[A] { self =>
  def apply(value: JsRead.Or): Option[A]

  def map[B](f: A => Option[B]): JsRead[B] =
    self(_).flatMap(f)

  def tryMap[B](f: A => B): JsRead[B] =
    map(a => Try(f(a)).toOption)
}

object JsRead {
  type Or = _ | _

  def apply[A](f: Or => Option[A]): JsRead[A] =
    f(_)

  implicit class Dsl(value: Or) {
    def as[A](implicit reader: JsRead[A]): Option[A] =
      reader(value)
  }

  implicit val string: JsRead[String] = JsRead(x => Try(x.asInstanceOf[String]).toOption)
  implicit val int: JsRead[Int]       = string.map(_.toIntOption)
  implicit val double: JsRead[Double] = string.map(_.toDoubleOption)
}
