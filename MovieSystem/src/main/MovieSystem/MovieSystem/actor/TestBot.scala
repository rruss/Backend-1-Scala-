package scala

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import MovieSystem.model.{Director, Movie, ErrorResponse, SuccessfulResponse}

object TestBot {

  case object TestCreate

  case object TestConflict

  case object TestRead

  case object TestUpdate

  case object TestDelete

  case object TestNotFound

  def props(manager: ActorRef) = Props(new TestBot(manager))
}

class TestBot(manager: ActorRef) extends Actor with ActorLogging {
  import TestBot._

  override def receive: Receive = {
    case TestCreate =>
      manager ! MovieManager.CreateMovie(Movie("1", "Joker", Director("dir-1", "Todd", None, "Philips"), 2019))

    case TestConflict =>
      manager ! MovieManager.CreateMovie(Movie("2", "Charlie's Angels", Director("dir-2", "Ivan", None, "Ivanov"), 2019))
      manager ! MovieManager.CreateMovie(Movie("2", "Test Test", Director("dir-2", "Ivan", None, "Ivanov"), 2019))

    case TestRead =>
      manager ! MovieManager.ReadMovie("1")

    case TestUpdate =>
      manager ! MovieManager.UpdateMovie(Movie("1", "Joker U", Director("dir-1", "Todd", None, "Philips"), 2019))

    case TestDelete =>
      manager ! MovieManager.DeleteMovie("2")

    case TestNotFound =>
    // FIXME: implement me
      manager ! MovieManager.DeleteMovie("7")


    case SuccessfulResponse(status, msg) =>
      log.info("Received Successful Response with status: {} and message: {}", status, msg)

    case ErrorResponse(status, msg) =>
      log.warning("Received Error Response with status: {} and message: {}", status, msg)

    case movie: Movie =>
      log.info("Received new movie: [{}]", movie)

    // TODO: add tests
  }
}