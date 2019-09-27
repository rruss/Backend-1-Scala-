package lab3.ShapingUp.task2

case class Rectangle(width: Double, height: Double) extends Rectangular {
  override def sides(): Int = 4

  override def perimeter(): Double = (width + height) * 2

  override def area(): Double = width * height
}
