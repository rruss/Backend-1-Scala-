import com.sksamuel.elastic4s.{Hit, HitReader, Indexable}
import icar.SprayJsonSerializer
import spray.json._
import icar.model.Autopart

import scala.util.Try

trait ElasticSerializer extends SprayJsonSerializer {

  // object -> JSON string
  implicit object AutopartIndexable extends Indexable[Autopart] {
    override def json(autopart: Autopart): String = autopart.toJson.compactPrint
  }

  // JSON string -> object
  // parseJson is a Spray method
  implicit object MovieHitReader extends HitReader[Autopart] {
    override def read(hit: Hit): Either[Throwable, Autopart] = {
      Try {
        val jsonAst = hit.sourceAsString.parseJson
        jsonAst.convertTo[Autopart]
      }.toEither
    }
  }
}