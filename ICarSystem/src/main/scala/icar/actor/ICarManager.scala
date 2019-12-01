package icar

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, RequestEntity}
import akka.stream.{ActorMaterializer, Materializer}
import com.sksamuel.elastic4s.http.ElasticDsl.{indexInto, _}
import com.sksamuel.elastic4s.http.HttpClient
import com.typesafe.config.{Config, ConfigFactory}
import
import model.{Autopart, ErrorResponse, SuccessfulResponse, Category}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}


object ICarManager {
  val index="books"
  val inIndex="_doc"
  case class GetAutoparts()
  case class CreateAutopart(autopart: Autopart)

  case class ReadAutopart(id:String)

  case class UpdateAutopart(autopart: Autopart)

  case class DeleteAutopart(id:String)


  def props(Client:HttpClient)=Props(new ICarManager(Client))
}
class ICarManager(Client:HttpClient) extends  Actor with ActorLogging with ElasticSerializer with Serializer{
  import ICarManager._

  implicit val system: ActorSystem = ActorSystem("telegram-service")
  implicit val materializer: Materializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  val config: Config = ConfigFactory.load() // config

  val token = config.getString("telegram.token") // token
  val url=s"https://api.telegram.org/bot730223470:AAEc4zoY4yzJNbKdlnRgaN0gIzMred8TKyM/sendMessage"


  override def receive:Receive={
    case CreateAutopart(autopart)=>
      val replyTo=sender()
      val cmd = Client.execute(indexInto(index / inIndex).id(autopart.id).doc(autopart))

      cmd.onComplete{
        case Success(value)=>
          log.info("Autopart with ID :{} created",autopart.id)
          replyTo ! Right(SuccessfulResponse(201,s"Autopart with ID: ${autopart.id} created"))
          val message:TelegramMessage=TelegramMessage(token, s"Autopart with ID = ${autopart.id} successfully created. Autopart = [$autopart]")
          val httpReq=Marshal(message).to[RequestEntity].flatMap{entity=>
            val request=HttpRequest(HttpMethods.POST,url,Nil,entity)
            log.debug("Request:{}",request)
            Http().singleRequest(request)
          }

          httpReq.onComplete{
            case Success(value)=>
              log.info(s"Response:$value")
              value.discardEntityBytes()
            case Failure(exception)=>
              log.error("error")
          }



        case Failure(_) =>
          log.warning(s"Could not create a autopart with ID: ${autopart.id} because it already exists.")
          replyTo ! Left(ErrorResponse(409, s"Autopart with ID: ${autopart.id} already exists."))
      }
    //
    //    case GetAllBooks()=>
    //      books.isEmpty match {
    //        case false=>
    //          sender()! Right(books)
    //        case true=>
    //          sender()! Left(ErrorResponse(404,"There are no books"))
    //      }

    case msg:ReadAutopart=>
      val replyTo=sender()
      val cmd=Client.execute{
        get(msg.id).from(index/inIndex)
      }
      cmd.onComplete{

        case Success(either)=>
          either.map(e => e.result.safeTo[Autopart]).foreach { autopart => {
            autopart match {
              case Left(_) =>
                log.info("Autopart with ID: {} not found [GET].", msg.id);
                replyTo ! Left(ErrorResponse(404, s"Autopart with ID: ${msg.id} not found [GET]."))
              case Right(autopart) =>
                log.info("Autopart with ID: {} found [GET].", msg.id)
                replyTo ! Right(autopart)

                val message:TelegramMessage=TelegramMessage(token,s"Autopart with ID = ${autopart.id} successfully got. Autopart = [$autopart]")

            val httpReq = Marshal(message).to[RequestEntity].flatMap { entity =>
            val request = HttpRequest(HttpMethods.GET, url, Nil, entity)
            log.debug("Request: {}", request)
            Http().singleRequest(request)
            }
            httpReq.onComplete {
            case Success(value) =>
            log.info(s"Response: $value")
            value.discardEntityBytes()

            case Failure(exception) =>
            log.error("error")
            }

            }
          }
          }
        case Failure(fail)=>
          log.warning(s"Could not read a book with ID: ${msg.id}. Exception with MESSAGE: ${fail.getMessage} occurred during this request. [GET]")
          replyTo ! Left(ErrorResponse(500, fail.getMessage))
      }

    case UpdateAutopart(autopart)=>
      val replyTo=sender()
      val cmd=Client.execute{
        update(autopart.id).in(index/inIndex).doc(autopart)
      }
      cmd.onComplete{
        case Success(_)=>
          log.info("Autopart with ID: {} updated.",autopart.id)
          replyTo ! Right(SuccessfulResponse(200,s"Autopart with ID:${autopart.id} updated"))


        case Failure(_)=>

      }

    case DeleteAutopart(id)=>
      val replyTo=sender()
      val cmd=Client.execute{
        delete(id).from(index/inIndex)
      }
      cmd.onComplete{
        case Success(either) =>
          either.map(e => e.result.result.toString).foreach { res => {
            res match {
              case "deleted" =>
                log.info("Autopart with ID: {} deleted.", id);
                replyTo ! Right(SuccessfulResponse(200, s"Autopart with ID: ${id} deleted."))

              case "not_found" =>
                log.info("Autopart with ID: {} not found [DELETE].", id);
                replyTo ! Left(ErrorResponse(404, s"Autopart with ID: ${id} not found [DELETE]."))
            }
          }
          }
        case Failure(fail) =>
          log.warning(s"Could not delete a autopart with ID: ${id}. Exception with MESSAGE: ${fail.getMessage} occurred during this request. [DELETE]")
          replyTo ! Left(ErrorResponse(500, fail.getMessage))
      }

  }




}