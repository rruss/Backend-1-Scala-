import Serializer.{Serializer, SprayJsonSerializer}
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import model.{Centre, ErrorResponse, Response, SuccessfulResponse, TelegramMessage}
import actor.ICarManager
import akka.http.scaladsl.marshalling.Marshal


import scala.util.{Failure, Success}
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._


object Boot extends App with SprayJsonSerializer with Serializer {


  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val config: Config = ConfigFactory.load() // config
  val log = LoggerFactory.getLogger("Boot")

  val token = "token" // config.getString("telegram.token") // token
  log.info(s"Token: $token")

  implicit val timeout: Timeout = Timeout(10.seconds)

  val icarManager = system.actorOf(ICarManager.props, "icar-manager")

  def SendMessage(msg: String): Unit ={
    val message: TelegramMessage = TelegramMessage(-273110234, msg)

    val httpReq = Marshal(message).to[RequestEntity].flatMap { entity =>
      val request = HttpRequest(HttpMethods.POST, s"https://api.telegram.org/bot${token}/sendMessage", Nil, entity)
      log.debug("Request: {}", request)
      Http().singleRequest(request)
    }

    httpReq.onComplete {
      case Success(value) =>
        log.info(s"Response: $value")
        value.discardEntityBytes()

      case Failure(exception) =>
        log.error(s"error: $exception")
    }

  }

    val route =
      path("check"){
        get {
          complete{
            "OK"
          }
        }
      } ~
        pathPrefix("icar"){
          path("centre" / Segment ){ centreID =>
            get{
              val output = (icarManager ? ICarManager.ReadCentre(centreID)).mapTo[Either[ErrorResponse, Centre]]
              onSuccess(output){
                case Right(centre) => {
                  SendMessage(s" Response: ${centre}")
                  complete(200, centre)
                }
                case Left(error) => {
                  SendMessage(s"Response: ${error.message}")
                  complete(error.status,error)
                }
              }
            }
          }~
            path("centre"/ Segment){ centreID =>
              delete {
                complete {
                  (icarManager ? ICarManager.DeleteCentre(centreID)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
                }
              }
            } ~
            path("centre"){
              post{
                entity(as[Centre]) { centre =>
                  val output = (icarManager ? ICarManager.CreateCentre(centre)).mapTo[Either[ErrorResponse, SuccessfulResponse]]

                  onSuccess(output) {
                    case Right(success) => {
                      SendMessage(s"${success.message}")
                      complete(output)
                    }
                    case Left(error) => {
                      SendMessage(s"${error.message}")
                      complete(output)
                    }
                  }
                }
              }
            }~
            path("centre"){
              put{
                entity(as[Centre]){ centre =>
                  complete{
                    (icarManager ? ICarManager.UpdateCentre(centre)).mapTo[Either[ErrorResponse,SuccessfulResponse]]
                  }
                }
              }
            }
        }


  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)
}
