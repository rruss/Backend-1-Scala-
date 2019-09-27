package lab3.TailRecursion

// IntList
sealed trait IntList{

  def length(l: Int = 0): Int = this match {
    case End => l
    case Node(_, tail) => tail.length(l + 1)
  }

  def product(p: Int = 1): Int = this match {
    case End => p
    case Node(head, tail) => tail.product(p * head)
  }

  def double: IntList = this match {
    case End => End
    case Node(head, tail) => Node(head * 2, tail.double)
  }

  //BONUS PART
  def map(f: Int => Int): IntList = this match {
    case End => End
    case Node(head, tail) => Node(f(head), tail.map(f))
  }

}
case object End extends IntList
case class Node(head: Int, tail: IntList) extends IntList




sealed trait GenericList[A] {

  def length(l: Int = 0): Int = this match {
    case GenericEnd() => l
    case GenericNode(head, tail) => tail.length(l + 1)
  }

  def map[B](f: A => B): GenericList[B] = this match {
    case GenericEnd() => GenericEnd()
    case GenericNode(head, tail) => GenericNode(f(head), tail.map(f))
  }
}

case class GenericEnd[A]() extends GenericList[A]
case class GenericNode[A](head: A, tail: GenericList[A]) extends GenericList[A]

object List extends App{


}
