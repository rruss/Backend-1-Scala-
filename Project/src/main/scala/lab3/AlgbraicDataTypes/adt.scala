package lab3.AlgbraicDataTypes

case class Calculator(num: Int){
  def isDivisibleBy3(num: Int): Unit = {
    num match {
      case num % 3 == 0 => println(num:3)
      case num =! 0 => println("It's not divisible by 3 !")
    }
  }
}

object adt{
  Calculator(10)
}
