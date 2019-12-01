package Serializer


import scala.util.Try
import com.sksamuel.elastic4s.{Hit, HitReader, Indexable}
import spray.json._
import model.Centre

trait ElasticSerializer extends SprayJsonSerializer{

  implicit object MangaIndexable extends Indexable[Centre] {
    override def json(centre: Centre): String = centre.toJson.compactPrint
  }

  // JSON string -> object
  // parseJson is a Spray method

  implicit object MovieHitReader extends HitReader[Centre] {
    override def read(hit: Hit): Either[Throwable, Centre] = {
      Try {
        val jsonAst = hit.sourceAsString.parseJson
        jsonAst.convertTo[Centre]
      }.toEither
    }
  }
}
