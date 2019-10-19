package scala

import akka.actor.{Actor, ActorLogging, Props}
import MovieSystem.model.{ErrorResponse, Movie, SuccessfulResponse}

object MovieManager {

  // Create
  case class CreateMovie(movie: Movie)

  // Read
  case class ReadMovie(id: String)

  // Update
  case class UpdateMovie(movie: Movie)

  // Delete
  case class DeleteMovie(id: String)

  def props() = Props(new MovieManager)
}

// know about existing movies
// can create a movie
// can manage movie
class MovieManager extends Actor with ActorLogging {

  // import companion OBJECT
  import MovieManager._

  var movies: Map[String, Movie] = Map()

  override def receive: Receive = {

    case CreateMovie(movie) =>
      movies.get(movie.id) match {
        case Some(existingMovie) =>
          log.warning(s"Could not create a movie with ID: ${movie.id} because it already exists.")
          sender() ! Left(ErrorResponse(409, s"Movie with ID: ${movie.id} already exists."))

        case None =>
          movies = movies + (movie.id -> movie)
          log.info("Movie with ID: {} created.", movie.id)
          sender() ! Right(SuccessfulResponse(201, s"Movie with ID: ${movie.id} created."))
      }

    case msg: ReadMovie =>
      movies.get(msg.id) match {
        case Some(movie) =>
          // TODO: logs
          log.info(s"Movie with ID: ${msg.id} has been returned.")
          sender() ! Right(movie)

        case None =>
          // TODO: logs
          log.warning(s"Movie with ID: ${msg.id} not found")
          sender() ! Left(ErrorResponse(404, s"Movie with ID: ${msg.id} not found."))
      }

    // TODO: UpdateMovie
    case UpdateMovie(movie) =>
      movies.get(movie.id) match {
        case Some(existingMovie) =>
          movies = movies + (movie.id -> movie)
          log.info(s"Movie with ID: ${movie.id} was updated.")
          sender() ! Right(SuccessfulResponse(202, s"Movie with ID: ${movie.id} has updated."))

        case None =>
          log.warning("Movie with ID: {} hadn't updated. Because, movie with such id not found.", movie.id)
          sender() ! Left(ErrorResponse(404, s"Movie with ID: ${movie.id} not found."))
      }

    // TODO: DeleteMovie
    case msg: DeleteMovie =>
      movies.get(msg.id) match {
        case Some(movie) =>
          movies = movies - movie.id
          log.info(s"Movie with ID: ${movie.id} was deleted.")
          sender() ! Right(SuccessfulResponse(204, s"Movie with ID: ${movie.id} was deleted."))

        case None =>
          // TODO: logs
          log.warning("Movie with ID: {} hadn't deleted. Because, movie with such id not found.", msg.id)
          sender() ! Left(ErrorResponse(404, s"Movie with ID: ${msg.id} not found."))
      }
  }

  def randomInt() =
  // FIXME: use random
    4
}