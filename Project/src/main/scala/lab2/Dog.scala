package lab2

case class Dog(dogName: String, barking: String = "Whooof") extends Walks with Animal {
  override def makeSound(): String = barking

  override def walk: String = super.walk

  override def name: String = this.dogName
}
