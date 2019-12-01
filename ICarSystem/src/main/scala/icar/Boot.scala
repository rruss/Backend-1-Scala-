package icar

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import icar.SprayJsonSerializer
import icar.ICarManager
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import icar.model.{Autopart, Category, ErrorResponse, SuccessfulResponse}


object Boot extends App with SprayJsonSerializer {

  implicit val system: ActorSystem = ActorSystem("icar-service")
  implicit val materializer: Materializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  implicit val timeout: Timeout = Timeout(10.seconds)

  val icarmanager = system.actorOf(ICarManager.props(), "icar-manager")


  val route =
    path("healthcheicarck") {
      get {
        complete {
          "OK"
        }
      }
    } ~
      pathPrefix("icar") {
        path("autopart" / Segment) { autopartId =>
          get {
            complete {
              (icarmanager ? ICarManager.ReadAutopart(autopartId)).mapTo[Either[ErrorResponse, Autopart]]
            }
          }
        } ~
          path("autopart") {
            post {
              entity(as[Autopart]) { autopart =>
                complete {
                  (icarmanager ? ICarManager.CreateAutopart(autopart)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
                }
              }
            }
          } ~
          path("autopart") {
            put {
              entity(as[Autopart]) { autopart =>
                complete {
                  (icarmanager ? ICarManager.UpdateAutopart(autopart)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
                }
              }
            }
          } ~
          path("autopart" / Segment) { autopartId =>
            delete {
              complete {
                (icarmanager ? ICarManager.DeleteAutopart(autopartId)).mapTo[Either[ErrorResponse, SuccessfulResponse]]
              }
            }
          }


      }


  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)
  /*
    // test create
    testBot ! TestBot.TestCreate
    // test conflict
    testBot ! TestBot.TestConflict
    testBot ! "bla-bla"
    // test read
    testBot ! TestBot.TestRead
    testBot ! TestBot.TestUpdate
    testBot ! TestBot.TestDelete
    testBot ! TestBot.TestNotFound*/

}