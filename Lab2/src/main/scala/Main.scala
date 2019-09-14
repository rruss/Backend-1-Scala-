
object Main extends App {
    // Here we will test Dog and Cat classes

    val dog1 = Dog("Ceasar")
    val dog2 = Dog("Laika")

    assert(dog1.name == "Ceasar")
    assert(dog2.name == "Laika")

    assert(dog1.makeSound() == "Whooof")
    assert(dog2.makeSound() == "Whooof")

    assert(dog1.walk == "Ceasar is walking")
    assert(dog2.walk == "Laika is walking")

    val cat1 = Cat("Tosha")
    val cat2 = Cat("Chocolate")

    assert(cat1.name == "Tosha")
    assert(cat2.name == "Chocolate")

    assert(cat1.makeSound() == "Miiyaaau")
    assert(cat2.makeSound() == "Miiyaaau")

    assert(cat1.walk == "Tosha is walking")
    assert(cat2.walk == "Chocolate is walking")

    println(dog1, dog2, cat1, cat2)
}
