package lab3.ShapingUp.task2

case class Square(side: Double) extends Rectangular {
  override def sides(): Int = 4

  override def perimeter(): Double = side * 4

  override def area(): Double = side * side
}
