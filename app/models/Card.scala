package models

object Card {
  import play.api.libs.functional.syntax._
  import play.api.libs.json.{Json, _}

  implicit val gameReads = Json.reads[GameInfo]
  implicit val gameWrites = Json.writes[GameInfo]

  implicit val rulingReads = Json.reads[Ruling]
  implicit val rulingWrites = Json.writes[Ruling]

  implicit val rulesReads = Json.reads[RulesInfo]
  implicit val rulesWrites = Json.writes[RulesInfo]

  implicit val typeReads = Json.reads[TypeInfo]
  implicit val typeWrites = Json.writes[TypeInfo]

  implicit val visualReads = Json.reads[VisualInfo]
  implicit val visualWrites = Json.writes[VisualInfo]

  implicit val collectorReads = Json.reads[CollectorInfo]
  implicit val collectorWrites = Json.writes[CollectorInfo]

  implicit val reads = Json.reads[Card]
  implicit val writes: Writes[Card] =
    ((JsPath \ "name").write[String] and
      (JsPath \ "imageUrl").write[String] and
      (JsPath \ "gameInfo").write[GameInfo] and
      (JsPath \ "rulesInfo").write[RulesInfo] and
      (JsPath \ "typeInfo").write[TypeInfo] and
      (JsPath \ "visualInfo").write[VisualInfo] and
      (JsPath \ "collectorInfo").write[CollectorInfo])(unlift(cardData))

  def cardData(a: Any) = a match {
    case c: Card => Some((
      c.name,
      c.imageUrl,
      c.gameInfo,
      c.rulesInfo,
      c.typeInfo,
      c.visualInfo,
      c.collectorInfo
    ))
    case _ => None
  }
}

case class Card(
  name: String,
  gameInfo: GameInfo,
  rulesInfo: RulesInfo,
  typeInfo: TypeInfo,
  visualInfo: VisualInfo,
  collectorInfo: CollectorInfo) {
  def imageUrl = "//mtgimage.com/card/%s.jpg".format(visualInfo.imageName)
}

case class GameInfo(
  manaCost: Option[String],
  text: Option[String],
  names: Option[Seq[String]],
  cmc: Option[Double],
  colors: Option[Seq[String]],
  flavor: Option[String],
  power: Option[String],
  toughness: Option[String],
  loyalty: Option[Int],
  hand: Option[Int],
  life: Option[Int])

case class RulesInfo(
  rulings: Option[Seq[Ruling]],
  legalities: Option[Map[String, String]])

case class Ruling(date: String, text: String)

case class TypeInfo(
  `type`: Option[String],
  superTypes: Option[Seq[String]],
  types: Seq[String],
  subtypes: Option[Seq[String]])

case class VisualInfo(
  imageName: String,
  layout: String,
  border: Option[String])

case class CollectorInfo(
  rarity: Option[String],
  artist: Option[String],
  number: Option[String],
  multiverseid: Option[String],
  variations: Option[Seq[String]],
  watermark: Option[String],
  timeshifted: Option[String],
  releaseDate: Option[String],
  reserved: Option[String],
  starter: Option[Boolean],
  source: Option[String])