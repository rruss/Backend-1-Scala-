package actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.index.CreateIndexResponse
import com.sksamuel.elastic4s.http.{HttpClient, RequestFailure, RequestSuccess}
import model.{Centre, ErrorResponse, Service, SuccessfulResponse}
import Serializer.ElasticSerializer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


object ICarManager {

  case class CreateCentre(centre: Centre)

  case class ReadCentre(id: String)

  case class UpdateCentre(centre: Centre)

  case class DeleteCentre(id: String)

  def props = Props(new ICarManager)
}



class ICarManager extends Actor with ActorLogging with ElasticSerializer{
  import ICarManager._

  val client = HttpClient(ElasticsearchClientUri("localhost", 9200))

  def createEsIndex() = {
    val cmd: Future[Either[RequestFailure, RequestSuccess[CreateIndexResponse]]] = client.execute{ createIndex("icar")}

    cmd.onComplete {
      case Success(value) =>
        value.foreach {requestSuccess =>
          println(requestSuccess)}

      case Failure(exception) =>
        println(exception.getMessage)
    }
  }

  def tackleResponse(centre: Centre, replyTo: ActorRef, sendCentre: Boolean, isSuccessful: Boolean, status: Int, answer: String): Unit ={
    if(sendCentre && isSuccessful){
      replyTo ! Right(centre)
    }
    else if(isSuccessful && !sendCentre){
      replyTo ! Right(SuccessfulResponse(status, answer))
    }
    else if(!isSuccessful && !sendCentre){
      replyTo ! Left(ErrorResponse(status, answer))
    }
  }

  def receive: Receive = {
    case CreateCentre(centre) =>

      val real_sender = sender()

      val cmd = client.execute(indexInto("icar" / "_doc").id(centre.id).doc(centre))

      cmd.onComplete {
        case Success(value) =>
          println(value)
          log.info("Centre with ID {} created.", centre.id)
          tackleResponse(centre, real_sender, false, true, 201, s"Centre with ID ${centre.id} created.")

        case Failure(exception) =>
          println(exception.getMessage)
          log.warning(s"Internal server error!")
          tackleResponse(centre, real_sender, false, false, 500, s"Internal server error!")
      }

    case ReadCentre(id: String) =>
      val real_sender = sender()

      val service1 = Service("id-1", "Sale")
      val centre1 = Centre("id-1", "Toyota", service1, 4.9)

      client.execute { get(id).from("icar" / "_doc") }.onComplete {
        case Success(either) =>
          either match {
            case Right(sr) =>
              if (sr.result.found) {
                val centre = sr.result.to[Centre]
                tackleResponse(centre, real_sender, true, true, 200, s"There is a centre with ID ${centre.id}")
                log.info("There is a centre with ID {}.", centre.id)
              } else {
                tackleResponse(centre1, real_sender, false, false, 404, s"Can not found centre with specified ID ${id}")
                log.error(s"Could not find a centre with ID ${id} within ReadCentre Request.")
              }
            case Left(left) =>
              tackleResponse(centre1, real_sender, false, false, left.status, "Internal Server Error Occurred!")
              log.error(left.toString)
          }
        case Failure(fail) =>
          println(fail.getMessage)
          log.error(s"Internal server error!")
          tackleResponse(centre1, real_sender, false, false, 500, s"Interval server error!")
      }

    case UpdateCentre(centre: Centre) =>
      val real_sender = sender()
      val service1 = Service("id-1", "Sale")
      val centre1 = Centre("id-1", "Toyota", service1, 4.9)

      val cmd = client.execute(update(centre.id).in("icar" / "_doc").doc(
        "id" -> centre.id,
        "title" -> centre.title,
        "service" -> Map(
          "id" -> centre.service.id,
          "name" -> centre.service.name
        ),
        "rating" -> centre.rating
      ))

      cmd.onComplete {
        case Success(either) =>
          either match {
            case Right(sr) =>
              if(sr.result.found) {
                tackleResponse(centre, real_sender, false, true, 200, s"Centre with ID ${centre.id} was updated.")
                log.info(s"Centre with ID ${centre.id} was updated.")
              } else {
                tackleResponse(centre, real_sender, false, false, 404, s"Can not found centre with specified ID ${centre.id}")
                log.error(s"Could not find a centre with ID ${centre.id} within UpdateCentre Request.")
              }

            case Left(left) =>
              tackleResponse(centre1, real_sender, false, false, left.status, s"Can not found centre with specified ID ${centre.id}")
              log.error(left.toString)
          }

        case Failure(fail) =>
          println(fail.getMessage)
          log.error(s"Internal server eror")
          tackleResponse(centre, real_sender, false, false, 500, s"Interval server error!")
      }

    case DeleteCentre(id: String) =>
      val real_sender = sender()

      val service1 = Service("id-1", "Sale")
      val centre1 = Centre("id-1", "Toyota", service1, 4.9)

      client.execute{ delete(id).from("icar"/"_doc")}.onComplete {
        case Success(either) =>
          either match {
            case Right(sr) =>
              if(sr.result.result != "not_found") {
                tackleResponse(centre1, real_sender, false, true, 200, s"Centre with ID ${id} was deleted.")
                log.info("Centre with ID {} has been deleted from database.", id)
              } else if(sr.result.result == "not_found") {
                tackleResponse(centre1, real_sender, false, false, 404, s"Centre with ID ${id} is not found in DeleteCentre request.")
                log.error(s"Could not find a centre with ID ${id} to Delete.")
              }
            case Left(left) =>
              tackleResponse(centre1, real_sender, false, false, left.status, "Internal Server Error Occurred!")
              log.error(left.toString)
          }


        case Failure(fail) =>
          println(fail.getMessage)
          log.error(s"Internal server error!")
          tackleResponse(centre1, real_sender, false, false, 500, s"Interval server error!")
      }
  }
}
