package lab3.ShapingUp.task1

case class Square(side: Double) extends Shape{
  override def sides(): Int = 4

  override def perimeter(): Double = side * 4

  override def area(): Double = side * side
}
