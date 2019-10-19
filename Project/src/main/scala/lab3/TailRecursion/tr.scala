package lab3.TailRecursion

// IntList
sealed trait IntList{

  def length(l: Int = 0): Int = this match {
    case End => l
    case Node(head, tail) => tail.length(l + 1)
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
  val intList = Node(1, Node(2, Node(3, Node(4, End))))


  println(intList.length())
  println(intList.tail.length() == 3)
  println(End.length() == 0)

  println(intList.product() == (1 * 2 * 3 * 4))
  println(intList.tail.product() == 2 * 3 * 4)
  println(End.product() == 1)

  println(intList.double == Node(1 * 2, Node(2 * 2, Node(3 * 2, Node(4 * 2, End)))))
  println(intList.tail.double == Node(4, Node(6, Node(8, End))))
  println(End.double == End)

  //BONUS PART
  println(intList.map(x => x * 3) == Node(1 * 3, Node(2 * 3, Node(3 * 3, Node(4 * 3, End)))))
  println(intList.map(x => 5 - x) == Node(5 - 1, Node(5 - 2, Node(5 - 3, Node(5 - 4, End)))))

  val genericList: GenericList[Int] = GenericNode(1, GenericNode(2, GenericNode(3, GenericEnd())))

  println(genericList.length())

  assert(genericList.map(x => x + 8) == GenericNode(1 + 8, GenericNode(2 + 8, GenericNode(3 + 8, GenericEnd()))))
  assert(genericList.map(x => x.toString) == GenericNode("1", GenericNode("2", GenericNode("3", GenericEnd()))))
}
