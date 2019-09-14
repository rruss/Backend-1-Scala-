case class Cat(catName: String, meow: String = "Miiyaaau") extends Walks with Animal{
  override def name: String = catName

  override def makeSound(): String = meow

  override def walk: String = super.walk
}
