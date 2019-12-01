package icar.model

case class Autopart(id: String, name: String, category: Category, manufacturer: Option[String], price: Int, count: Int)
