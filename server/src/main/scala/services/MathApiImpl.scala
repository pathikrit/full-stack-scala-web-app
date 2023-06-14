package services

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
