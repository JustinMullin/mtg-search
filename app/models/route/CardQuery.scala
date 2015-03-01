package models.route

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{Reads, JsPath, Json}

object CardQuery {
  case class QueryField[T](name: String, dbName: String, example: T)
  
  val fields = Seq[QueryField[_]](
    QueryField[String](
      "name", "name", ""),
    QueryField[String](
      "text", "gameInfo.text", ""),
    QueryField[Set[String]](
      "color", "gameInfo.color", Set()),
    QueryField[Int](
      "cmc", "gameInfo.cmc", 0),
    QueryField[Int](
      "power", "gameInfo.power", 0),
    QueryField[Int](
      "toughness", "gameInfo.toughness", 0),
    QueryField[Int](
      "loyalty", "gameInfo.loyalty", 0),
    QueryField[Set[String]](
      "legality", "gameInfo.legality", Set()),
    QueryField[Set[String]](
      "superTypes", "gameInfo.superTypes", Set()),
    QueryField[Set[String]](
      "types", "gameInfo.types", Set()),
    QueryField[Set[String]](
      "subTypes", "gameInfo.subTypes", Set())
  )
}

case class QueryPair[T](value: T, mode: String)
object QueryPair {
  implicit def reads[T](implicit r: Reads[T]) =
    ((JsPath \ "value").read[T] and
    (JsPath \ "mode").read[String])(QueryPair.apply[T] _)
}

case class QueryMode(label: String, code: String)
object QueryMode {
  implicit val reads = Json.reads[QueryMode]
}

class TextMode(label: String, code: String) extends QueryMode(label, code)
object TextMode {
  object Exact extends TextMode("Exact Match", "exact")
  object Regex extends TextMode("Regular Expression", "regex")
  object Words extends TextMode("Whole Words", "words")
  object Anywhere extends TextMode("Any Match", "anywhere")
}

class NumericMode(label: String, code: String, filter: (Double, Double) => Boolean) extends QueryMode(label, code)
object NumericMode {
  object LessThan extends NumericMode("<", "lt", _ < _)
  object LessThanOrEqual extends NumericMode("≤", "lte", _ <= _)
  object GreaterThan extends NumericMode(">", "gt", _ > _)
  object GreaterThanOrEqual extends NumericMode("≥", "gte", _ >= _)
  object Equal extends NumericMode("=", "eq", _ == _)
  object NotEqual extends NumericMode("≠", "ne", _ != _)
}

class SetMode(label: String, code: String) extends QueryMode(label, code)
object SetMode {
  object Some extends SetMode("Some", "some")
  object None extends SetMode("None", "none")
  object All extends SetMode("All", "all")
  object ExactlyAll extends SetMode("Exactly All", "exact")
  object NoOther extends SetMode("No Others", "noOther")
}
