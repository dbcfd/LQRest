package com.webwino.rest

import cc.spray._
import cc.spray.json._
import directives.Remaining

import com.webwino.models.User

trait Rest extends Directives {
  object resultCodes {
    val success = JsNumber(200)
    val failure = JsNumber(400)
    val idNotFound = JsNumber(401)
    val duplicateId = JsNumber(402)
  }
  
  val restService = {
    path("test") {
      get { _.complete("Say hello to Spray!") }
    } ~
    path("api" / "users" / Remaining) { id =>
      get { ctx => ( {
        ctx.complete( (
          User.fromDb(id) match {
            case Some(user:User) => ( {
              val jsUser = user.toJson
              JsObject(Map("resultCode" -> resultCodes.success) ++ jsUser.fields)
            })
            case None => {
              JsObject("resultCode" -> resultCodes.idNotFound)
            }
          }
        ).toString() )
      } ) } ~
      put { ctx => ({
        User.fromDb(id) match {
          case Some(_) => ctx.complete(JsObject("result" -> resultCodes.duplicateId).toString())
          case None => ( {
            val user = new User(id)
            User.toDb(user)
            ctx.complete(JsObject("result" -> resultCodes.success).toString())
          })
        }
      })} ~
      post { ctx => ( {
        User.fromDb(id) match {
          case Some(_) => ctx.complete(JsObject("result" -> resultCodes.success).toString())
          case None => ( {
            ctx.complete(JsObject("result" -> resultCodes.idNotFound).toString())
          })
        }
      } ) } ~
        delete { ctx => ( {
          User.fromDb(id) match {
            case Some(user) => ( {
              User.delete(user)
              ctx.complete(JsObject("result" -> resultCodes.idNotFound).toString())
            } )
            case None => ctx.complete(JsObject("result" -> resultCodes.idNotFound).toString())
          }
        } ) }
    }
  }
  
}