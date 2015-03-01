package controllers

import java.util.regex.Pattern
import javax.inject.Singleton

import models.Card
import models.route.CardQuery
import models.route.CardQuery.QueryField
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.ImplicitBSONHandlers.JsObjectWriter
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.QueryOpts
import reactivemongo.core.commands.Count

@Singleton
class Cards extends Controller with MongoController {

  def collection = db.collection[JSONCollection]("cards")

  import models.Card.{reads, writes}

  def findCards = Action.async(parse.json) { request =>
    val query = request.body.as[JsObject]

    val page = (query \ "page").as[Int]
    val pageSize = (query \ "pageSize").as[Int]

    val parameters = (CardQuery.fields map {
      case QueryField(name, dbName, s: String) =>
        val pair = (query \ name).as[JsObject]
        val value = (pair \ "value").as[String]
        if(value.nonEmpty) {
          Some(dbName -> (Json.obj("$regex" -> Pattern.quote(value), "$options" -> "i"): Json.JsValueWrapper))
        } else {
          None
        }
      case _ => None
    }).flatten

    /*val parameters = for (
      field <- CardQuery.fields;
      rawValue <- field.transform(query);
      value = rawValue.trim
      if value.nonEmpty) yield {
      field.dbName -> (Json.obj("$regex" -> Pattern.quote(value), "$options" -> "i"): Json.JsValueWrapper)
    }*/

    val cursor = collection.
      find(Json.obj(parameters:_*)).
      sort(Json.obj("name" -> 1)).
      options(QueryOpts(page*pageSize, pageSize))
      .cursor[Card]

    val count = db.command(Count("cards", Some(JsObjectWriter.write(Json.obj(parameters:_*)))))

    val cards = cursor.collect[List](pageSize)

    for(cards <- cards; count <- count) yield {
      Ok(Json.obj("results" -> count, "maxPage" -> count/pageSize, "cards" -> Json.toJson(cards)))
    }
  }

}

