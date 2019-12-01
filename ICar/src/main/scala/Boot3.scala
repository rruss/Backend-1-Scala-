//import java.io.{File, FileFilter}
//import java.util
//
//import Serializer.SprayJsonSerializer
//import actor.FileManager
//import com.amazonaws.auth.AWSStaticCredentialsProvider
//import com.amazonaws.auth.BasicAWSCredentials
//import com.amazonaws.regions.Regions
//import com.amazonaws.services.s3.AmazonS3
//import com.amazonaws.services.s3.AmazonS3ClientBuilder
//import com.amazonaws.services.s3.model._
//import org.slf4j.LoggerFactory
//import akka.actor.ActorSystem
//import akka.http.javadsl.model.StatusCodes
//import akka.http.scaladsl.Http
//import akka.stream.ActorMaterializer
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.server.Directives._
//import akka.pattern.ask
//import akka.util.Timeout
//import spray.json.DefaultJsonProtocol._
//import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
//import akka.http.scaladsl.marshalling.ToResponseMarshallable
//import com.amazonaws.AmazonServiceException
//import org.slf4j.LoggerFactory
//
//import scala.concurrent.Future
//import model.{Centre, ErrorResponse, SuccessfulResponse}
//
//import scala.concurrent.duration._
//import collection.JavaConverters._
//
//case class Response(msg: String, statusCode: StatusCode)
//
//object Boot3 extends App with SprayJsonSerializer {
//  var bucketName: String = "bhle-lab"
//  var mainPath: String = "src/main/resources/"
//  implicit val timeout = Timeout(30.seconds)
//
//  // needed to run the route
//  implicit val system = ActorSystem()
//
//  implicit val materializer = ActorMaterializer()
//  // needed for the future map/flatmap in the end and future in fetchItem and saveOrder
//  implicit val executionContext = system.dispatcher
//
//  val log = LoggerFactory.getLogger("Boot")
//
//  val awsCreds = new BasicAWSCredentials(
//    "",
//    "")
//
//  // Frankfurt client
//  val s3Client: AmazonS3 = AmazonS3ClientBuilder.standard
//    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
//    .withRegion(Regions.EU_CENTRAL_1)
//    .build
//  // check if bucket exists
//
//  if (s3Client.doesBucketExistV2("bhle-lab")) {
//    log.info("Bucket exists")
//  } else {
//    s3Client.createBucket("bhle-lab")
//    log.info("Bucket created")
//  }
//  val s3interaction = system.actorOf(FileManager.props(s3Client, mainPath + "s3/", bucketName), "s3interaction")
//
//
//  // file for output
//  val outFile = new File(mainPath + "/out")
//
//  // file for input
////
////  val io = system.actorOf(IO.props(s3Client, bucketName, outFile, mainPath))
//
//  val route = path("s3") {
//    get {
//      parameters('path.as[String]) { path =>
//        complete {
//          (s3interaction ? FileManager.Download(path)).mapTo[SuccessfulResponse]
//        }
//      }
//    } ~
//      post {
//        entity(as[Centre]) { centre =>
//          complete {
//            (s3interaction ? FileManager.Upload(path)).mapTo[SuccessfulResponse]
//          }
//        }
//      }
//  }
////  }~pathPrefix("task2") {
////    path("out") {
////      get {
////        complete {
////          (io ? IO.OUT).mapTo[ErrorInfo]
////        }
////      }
////    }~path("in") {
////      get {
////        complete {
////          (io ? IO.IN).mapTo[ErrorInfo]
////        }
////      }
////    }
////  }
//
//
//
//  val bindingFuture = Http().bindAndHandle(route, "localhost", 8000)
//  log.info("Listening on port 8000...")
//
//  /*  println("qwe")
//    val listObjectsRequest = new ListObjectsRequest().
//      withBucketName(bucketName)
//    var list: ObjectListing = s3Client.listObjects(bucketName)
//    list.getObjectSummaries().forEach(key => println(key.getKey()))*/
//
//}