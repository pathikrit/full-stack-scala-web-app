package framework

class ValidationSuite extends munit.FunSuite {
  test("validation") {
    import Validation.Dsl
    import ValidationSuite._

    val person = Person(
      name = "rick",
      address = Address(
        street = "23",
        city = "NA",
        zip = "ok",
        country = "",
      ),
    )
    assertEquals(
      person.validate,
      Left(
        List(
          "address street must be valid",
          "name must start with capital letter",
          "address zip must be of length 5",
          "address country must be valid",
        ),
      ),
    )
  }
}

object ValidationSuite {
  case class Address(street: String, city: String, zip: String, country: String)
  object Address {
    implicit val validator: Validation[Address] = Validation[Address]
      .rule("street must be valid")(_.street.length > 5)
      .rule("zip must be of length 5")(_.zip.length == 5)
      .rule("country must be valid")(_.country.length == 2)
  }

  case class Person(name: String, address: Address)
  object Person {
    implicit val validator: Validation[Person] = Validation[Person]
      .rule("name must exist")(_.name.nonEmpty)
      .rule("name must start with capital letter")(_.name(0).isUpper)
      .validate("address")(_.address)
  }
}
