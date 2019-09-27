package lab3.ShapingUp.task3

sealed trait Shape {
  def sides(): Int
  def perimeter(): Double
  def area(): Double
}

trait Rectangular extends Shape {
  override def sides(): Int = 4

}

case class Circle(radius: Double) extends Shape{
  override def sides(): Int = 0

  override def perimeter(): Double = 2 * Math.PI * radius

  override def area(): Double = Math.PI * radius * radius
}

case class Square(side: Double) extends Rectangular {
  override def sides(): Int = 4

  override def perimeter(): Double = side * 4

  override def area(): Double = side * side
}

case class Rectangle(width: Double, height: Double) extends Rectangular {
  override def sides(): Int = 4

  override def perimeter(): Double = (width + height) * 2

  override def area(): Double = width * height
}

object Draw {

  def apply(shape: Shape): Unit = {
    shape match {
      case unit: Circle => println(s"A circle with a radius ${unit.radius} " +
        s"has ${unit.sides()} sides, a perimeter ${unit.perimeter()} and an area ${unit.area()}")
      case unit: Rectangle => println(s"A rectangle with a width ${unit.width} " +
        s"and a height ${unit.height} has ${unit.sides()} sides, a perimeter ${unit.perimeter()}" +
        s" and an area ${unit.area()}")
      case unit: Square => println(s"A circle with a side ${unit.side} " +
        s"has ${unit.sides()} sides, a perimeter ${unit.perimeter()} and an area ${unit.area()}")
      case _ => println("We have only Circle, Square and Rectangle")
    }
  }

}

object Main extends App{
  Draw(Circle(10))
  Draw(Rectangle(5, 4))
  Draw(Square(10))
}