package com.example

import akka.actor.Actor
import spray.routing._
import spray.http._
import java.io.File
import com.mongodb.casbah.Imports._

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
  
  val db = MongoClient("localhost", 27017)("hello")("hello")
  
  case class Name(fname: String, lname:String) {
    require(fname.size > 0, "First name cannot be blank")
    require(lname.size > 0, "Last name cannot be blank")
  }
  
  val myRoute = path("hello") { 
      get{
        parameters('fname.as[String], 'lname.as[String]).as(Name) { name =>
          complete {
            db.save(MongoDBObject("fname" -> name.fname, "lname" -> name.lname))
            "Hello, " + name.fname + " " + name.lname + "!"
          }
        }
      }
    }~
    path("list") {
      get{        
        complete{
          db.find().toList.map(_.toString()) mkString("\n")
        }
      }
    }
}