package icar

import icar.model.{Autopart, Category, ErrorResponse, SuccessfulResponse}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait SprayJsonSerializer extends DefaultJsonProtocol {
  // custom formats
  implicit val directorFormat: RootJsonFormat[Category] = jsonFormat2(Category)
  implicit val movieFormat: RootJsonFormat[Autopart] = jsonFormat6(Autopart)

  implicit val successfulResponse: RootJsonFormat[SuccessfulResponse] = jsonFormat2(SuccessfulResponse)
  implicit val errorResponse: RootJsonFormat[ErrorResponse] = jsonFormat2(ErrorResponse)
}
