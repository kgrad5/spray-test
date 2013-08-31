package com.example

import akka.actor.Actor
import spray.routing._
import com.github.nscala_time.time.Imports._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.conversions.scala._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  RegisterConversionHelpers()
  RegisterJodaTimeConversionHelpers()
  val db = MongoClient("localhost", 27017)("logserver")("logs")

  case class LogItem(app: String, payload: String) {
    require(app match {
      case "iTAM" => true
      case _ => false
    }, "Application not recognized")
    require(payload.length() > 0, "Payload must be non-empty")
  }

  val myRoute = path("log") {
    get {
      parameters('app.as[String], 'payload.as[String]).as(LogItem) { item =>
        complete {
          val record = MongoDBObject("app" -> item.app, "payload" -> item.payload, "time" -> DateTime.now)
          db.save(record)
          db.findOne(record) mkString
        }
      }
    }
  } ~
    path("list") {
      get {
        complete {
          db.find().toList.map(_.toString()) mkString ("\n")
        }
      }
    } ~
    path("list-today") {
      get {
        complete {
          val start = DateTime.yesterday.hour(23).minute(59).second(59)
          val end = DateTime.now.hour(23).minute(59).second(59)
          val query: DBObject = $and("time" $gt start $lte end)
          db.find(query).toList.map(_.toString()) mkString ("\n")
        }
      }
    }

}