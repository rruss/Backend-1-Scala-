package actor

import java.io.File

import akka.actor.{Actor, Props}
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import com.amazonaws.AmazonServiceException
import model.{SuccessfulResponse, ErrorResponse}
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.{GetObjectRequest, PutObjectResult}

object FileManager {

  def props(s3Client: AmazonS3, mainPath: String, bucketName: String) = Props(new FileManager(s3Client, mainPath, bucketName))

  case class Download(path: String)

  case class Upload(path: String)
}

class FileManager(s3Client: AmazonS3, mainPath: String, bucketName: String) extends Actor{
  import FileManager._

  override def preStart() = println("FileManager actor created")


  override def receive: Receive = {
    case Download(path) =>

      println(s"Download request with path: $path")
      if (s3Client.doesObjectExist(bucketName, path)) {
        downloadFromS3(path)
        sender() ! SuccessfulResponse(200, "OK")
      }else {
        sender() ! ErrorResponse(404, "Not Found")
      }
    case Upload(path) =>
      println(s"Upload request with path: $path")
      if (uploadToS3(path)) {
        sender() ! SuccessfulResponse(200, "OK")
      }else {
        sender() ! ErrorResponse(404, "Not Found")
      }
  }

  def uploadToS3(path: String): Boolean = {
    val file = new File(mainPath + path)
    if (!file.exists()) {
      return false;
    }
    s3Client.putObject(bucketName, path, file)
    println(s"uploaded to s3 with path: $path")
    return true;
  }

  def fileIsUploadedToS3(uploadPath: String): Boolean = {
    return s3Client.doesObjectExist(bucketName, uploadPath)
  }

  def downloadFromS3(uploadPath: String) {
    val downloadPath: String = mainPath + uploadPath

    if(!fileIsUploadedToS3(uploadPath)) {
      throw new RuntimeException(s"File $uploadPath is not uploaded!")
    }

    var dirPath: String = downloadPath.substring(0, downloadPath.lastIndexOf('/'))
    var newDir = new File(dirPath)
    newDir.mkdir()

    s3Client.getObject(new GetObjectRequest(bucketName, uploadPath),
      new File(downloadPath))
    println(s"downloaded from s3 to path: $uploadPath")
  }

}