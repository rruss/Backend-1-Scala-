package lab4

import lab4.main.directors

case class Film(name: String,
                 yearOfRelease: Int,
                 imdbRating: Double)

case class Director( firstName: String,
                     lastName: String,
                     yearOfBirth: Int,
                     films: Seq[Film])


object main extends App {
  val memento = new Film("Memento", 2000, 8.5)
  val darkKnight = new Film("Dark Knight", 2008, 9.0)
  val inception = new Film("Inception", 2010, 8.8)
  val highPlainsDrifter = new Film("High Plains Drifter", 1973, 7.7)
  val outlawJoseyWales = new Film("The Outlaw Josey Wales", 1976, 7.9)
  val unforgiven = new Film("Unforgiven", 1992, 8.3)
  val granTorino = new Film("Gran Torino", 2008, 8.2)
  val invictus = new Film("Invictus", 2009, 7.4)
  val predator = new Film("Predator", 1987, 7.9)
  val dieHard = new Film("Die Hard", 1988, 8.3)
  val huntForRedOctober = new Film("The Hunt for Red October", 1990, 7.6)
  val thomasCrownAffair = new Film("The Thomas Crown Affair", 1999, 6.8)
  val eastwood = new Director("Clint", "Eastwood", 1930,
    Seq(highPlainsDrifter, outlawJoseyWales, unforgiven, granTorino, invictus))
  val mcTiernan = new Director("John", "McTiernan", 1951,
    Seq(predator, dieHard, huntForRedOctober, thomasCrownAffair))
  val nolan = new Director("Christopher", "Nolan", 1970,
    Seq(memento, darkKnight, inception))
  val someGuy = new Director("Just", "Some Guy", 1990,
    Seq())
  val directors: Seq[Director] = Seq(eastwood, mcTiernan, nolan, someGuy)


    //tasks --------------------------------------------------------------------------------------

  def directorsWithMoreFilms(numberOfFilms: Int): Unit = {
    println(directors.filter(d => d.films.length > numberOfFilms))
  }
  def findDirectorsBornEarly(year: Int): Unit = {
    println(directors.find(director => director.yearOfBirth < year))
  }
  def moreFilmsBornEarly(numberOfFilms: Int, year: Int): Unit = {
    println(directors.filter(director => director.films.length > numberOfFilms && director.yearOfBirth < year))
  }
  def sortDirectorsByAge(isAscending: Boolean): Unit = {
    isAscending match {
      case true => println(directors.sortWith(_.yearOfBirth < _.yearOfBirth))
      case false => println(directors.sortWith(_.yearOfBirth > _.yearOfBirth))
      case _ => println(directors.sortWith(_.yearOfBirth < _.yearOfBirth))
    }
  }

  val nolanFilms = nolan.films.map(film => film.name)
  val cinephile = directors.flatMap(director => director.films.map(film => film.name))
  val vintageMcTiernan = mcTiernan.films.map(film => film.yearOfRelease).min
  val highScoreTable = directors.flatten(director => director.films).sortWith(
    (f1, f2) => f1.imdbRating > f2.imdbRating)

  def averageRating() : Double = {
    val films: Seq[Film] = directors.flatten(director => director.films)
    val sum: Double = films.foldLeft(0.0)((sum, film) => sum + film.imdbRating)
    sum / films.size
  }
  def tonightListings =  directors.foreach(d => d.films.foreach(
    f => println(s"Tonight only! ${f.name} by ${d.firstName} !")))
  def fromArchives(): Option[Film] = {
    val sorted = directors.flatten(d => d.films).sortWith(
      (f1, f2) => (f1.yearOfRelease) < f2.yearOfRelease)
    sorted.headOption
  }

  directorsWithMoreFilms(4)
  (findDirectorsBornEarly(2000))
  (moreFilmsBornEarly(2, 1990))
  (sortDirectorsByAge(true))
  println(nolanFilms)
  println(cinephile)
  println(vintageMcTiernan)
  println(highScoreTable)
  println(averageRating())
  (tonightListings)
  println(fromArchives())

}
