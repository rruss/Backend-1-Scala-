package lab3.AlgbraicDataTypes

case class Integer(res: Int){
  def result(): Int = res
}
case class Str(res: Double){
  def result(): String = "Input isn't type of integer !"

}

object Calculator{
  def apply(num1: Int, num2: Int): Unit = {
    val res = num1 + num2
    if (res % 3 == 0) println(Integer(res))
    else if (res % 3 != 0) println(Str(res))
    }
}

sealed trait Source{
}
case class Well(size: Int, isCarbonated: Boolean) extends Source {
  def isCarbon(): String = {
    if(isCarbonated) "carbonated"
    else "not carbonated"
  }
}
case class Spring(size: Int, isCarbonated: Boolean) extends Source {
  def isCarbon(): String = {
    if(isCarbonated) "carbonated"
    else "not carbonated"
  }
}
case class Tap(size: Int, isCarbonated: Boolean) extends Source {
  def isCarbon(): String = {
    if(isCarbonated) "carbonated"
    else "not carbonated"
  }
}

object Water{
  def apply(source: Source): Unit = {
    source match {
      case w: Well => println(s"A water from well is ${w.isCarbon()} and has a size ${w.size} litres")
      case w: Spring => println(s"A water from spring is ${w.isCarbon()} and has a size ${w.size} litres")
      case w: Tap => println(s"A water from tap is ${w.isCarbon()} and has a size ${w.size} litres")
    }
  }
}

object adt extends App {
  Calculator(10, 4)
  Water(Spring(13, true))
}
