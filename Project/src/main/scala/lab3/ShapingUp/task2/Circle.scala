package lab3.ShapingUp.task2

case class Circle(radius: Double) extends Shape{
  override def sides(): Int = 0

  override def perimeter(): Double = 2 * Math.PI * radius

  override def area(): Double = Math.PI * radius * radius
}
