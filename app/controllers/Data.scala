package controllers

import models._
import org.slf4j.LoggerFactory
import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, _}
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

object Data {
  val SourceUrl = "//mtgjson.com/json/AllCards-x.json"

  import models.Card._

  implicit val externalReads =
    ((JsPath \ "name").read[String] and
      JsPath.read[GameInfo] and
      JsPath.read[RulesInfo] and
      JsPath.read[TypeInfo] and
      JsPath.read[VisualInfo] and
      JsPath.read[CollectorInfo])(Card.apply _)
}

class Data extends Controller with MongoController {
  import controllers.Data.externalReads

  val logger = LoggerFactory.getLogger(classOf[Data])

  def collection = db.collection[JSONCollection]("cards")

  def loadData = Action {
    val data = Json.fromJson[Map[String, JsObject]](
      Json.parse(
        Source.fromURL(Data.SourceUrl, "utf-8").mkString))

    val cards = data map { cards =>
      cards.map(_._2).toSeq
    }

    logger.debug("Clearing cards DB.")
    collection.drop()

    cards.map { jsonCards =>
      val cards = jsonCards map { jsonCard =>
        val cardResult = Json.fromJson[Card](jsonCard)

        cardResult match {
          case JsSuccess(card, _) =>
            logger.info("Creating card: \"%s\".".format(card.name))
            collection.insert(Json.toJson(card))
            Some(card)
          case JsError(errors) =>
            logger.error("Failed to parse card \"%s\".  %s".format((jsonCard \ "name").as[String], errors.mkString("; ")))
            None
        }
      }

      val count = cards.count(_.isDefined)

      Ok(s"$count card(s) loaded.")
    }.getOrElse(Ok("Failed to load card data."))
  }
}
