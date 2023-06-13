package framework

case class Validation[A](rules: Map[String, A => Boolean]) {
  def rule(msg: String)(check: A => Boolean): Validation[A] =
    copy(rules = rules + (msg -> check))

  def rule(validator: Validation[A]): Validation[A] =
    copy(rules = rules ++ validator.rules)

  def validate[B](prefix: String)(f: A => B)(implicit validation: Validation[B]): Validation[A] =
    rule(validation.contramap(prefix + " " + _)(f))

  def contramap[B](modifyMsg: String => String)(f: B => A): Validation[B] =
    Validation[B](rules = rules.map({ case (msg, rule) => modifyMsg(msg) -> rule.compose(f) }))

  def apply(a: A): Validation.Result[A] =
    rules.collect({ case (msg, f) if !f(a) => msg }).toList match {
      case Nil        => Right(a)
      case violations => Left(violations)
    }
}

object Validation {
  type Result[A] = Either[List[String], A]

  def uniqueBy[A, B](as: Iterable[A])(f: A => B): Boolean =
    as.groupBy(f).values.forall(_.sizeCompare(1) == 0)

  def empty[A]: Validation[A] =
    Validation(Map.empty[String, A => Boolean])

  def apply[A]: Validation[A] =
    empty

  def apply[A](a: A)(implicit f: Validation[A]): Result[A] =
    f(a)

  implicit class Dsl[A: Validation](a: A) {
    def validate: Result[A] = Validation(a)
    def validateUnsafe: A = validate match {
      case Left(violations) => throw new IllegalArgumentException(violations.mkString(";"))
      case Right(a)         => a
    }
  }
}
