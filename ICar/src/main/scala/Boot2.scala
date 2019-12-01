import Serializer.Serializer
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import model.TelegramMessage
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Boot2 extends App with Serializer {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val config: Config = ConfigFactory.load() // config
  val log = LoggerFactory.getLogger("Boot")


  val token = "830958142:AAEoBbyilh8scXkzGAJWsN8qjluCXyqAJLU" // config.getString("telegram.token") // token
  log.info(s"Token: $token")



  val message: TelegramMessage = TelegramMessage(-352088280, "Hello world from icar service")

  val httpReq = Marshal(message).to[RequestEntity].flatMap { entity =>
    val request = HttpRequest(HttpMethods.POST, s"https://api.telegram.org/bot$token/sendMessage", Nil, entity)
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

  Thread.sleep(5000)


}
