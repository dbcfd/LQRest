package com.webwino.models

import com.webwino.mongo.Mongo

import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject

class User(val foursquareId:String) {

}

object User {
  val collection = Mongo.userCollection
  
  def fromDb(foursquareId:String):Option[User] = {
    val dbUser = MongoDBObject("foursquareId" -> foursquareId)
    val found = collection.findOne(dbUser)
    found match {
      case Some(user:MongoDBObject) => ( {
        return Some(new User(user.as[String]("foursquareId")))
      })
      case _ => return None
    }
  }

  def toDb(user:User) = {
    val dbUser = MongoDBObject("foursquareId" -> user.foursquareId)
    collection.save(dbUser)
  }

}