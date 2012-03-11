package com.webwino.models

import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.Imports._

import com.webwino.mongo.Mongo
import cc.spray.json._

class User(val dbObject:MongoDBObject) {
  def this(fqId:String) = this(MongoDBObject("foursquareId" -> fqId))
  def foursquareId:String = dbObject.as[String]("foursquareId")

  def toJson = JsObject("foursquareId" -> JsString(foursquareId))
}

object User {
  val collection = Mongo.userCollection
  
  def fromDb(foursquareId:String):Option[MongoDBObject] = {
    val dbUser = MongoDBObject("foursquareId" -> foursquareId)
    val found = collection.findOne(dbUser)
    found match {
      case Some(_) => Some(dbUser)
      case _ => None
    }
  }

  def fromJson(v:JsValue) = v.asJsObject.getFields("foursquareId") match {
    case Seq(JsString(foursquareId)) => ( {
      val dbObj = MongoDBObject("foursquareId" -> foursquareId)
      new User(dbObj)
    } )
    case _ => deserializationError("Incorrect format:" + v.toString())
  }

  def toDb(user:MongoDBObject) = {
    collection.save(user)
  }
  
  def delete(user:MongoDBObject) = {
    collection.remove(user)
  }
  
  implicit def dbToUser(dbObj:MongoDBObject):User = new User(dbObj)
  implicit def userToDb(user:User):MongoDBObject = user.dbObject
}