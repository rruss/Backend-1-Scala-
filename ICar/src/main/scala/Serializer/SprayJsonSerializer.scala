package Serializer

import model.{Service, ErrorResponse, Centre, SuccessfulResponse}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait SprayJsonSerializer extends DefaultJsonProtocol {
  implicit val authorFormat: RootJsonFormat[Service] = jsonFormat2(Service)
  implicit val mangaFormat: RootJsonFormat[Centre] = jsonFormat4(Centre)

  implicit val successfulResponse: RootJsonFormat[SuccessfulResponse] = jsonFormat2(SuccessfulResponse)
  implicit val errorResponse: RootJsonFormat[ErrorResponse] = jsonFormat2(ErrorResponse)
}
