package com.webwino.rest

import java.net.URLEncoder

import cc.spray._
import cc.spray.http._
import cc.spray.json._
import directives.Remaining
import cc.spray.utils._

import com.webwino.models.User
import http.HttpHeaders.Location

/**
 * Rest directives. Each path handled must also correspond to a path specified in webapp/web.xml
 */
trait Rest extends Directives with Logging {
  object resultCodes {
    val success = JsNumber(200)
    val userNotCreated = JsNumber(201)
    val failure = JsNumber(400)
    val idNotFound = JsNumber(401)
    val duplicateId = JsNumber(402)
  }

  private val gameRedirectUri:String = "http://localhost:8080/game"
  
  val restService = {
    path("test") {
      get { _.complete("Say hello to Spray!") }
    } ~
    path ("foursquare" / "callback" / Remaining) { token =>
      get { ctx => ( {
        User.fromAccessToken(token) match {
          case Some(user:User) => ( {
            JsObject(Map("resultCode" -> resultCodes.success) ++ user.toJson.fields)
            /**
            val contentType:ContentType = new ContentType("text/html");
            ctx.complete {
              HttpResponse(
                status = StatusCodes.Found,
                headers = Location(gameRedirectUri) :: Nil,
                content = StatusCodes.Found.htmlTemplate.toOption.map(s => HttpContent("text/html", s format gameRedirectUri)),
                protocol = HttpProtocols.`HTTP/1.1`
              )
            }
             **/
          })
          case None => {
            JsObject("resultCode" -> resultCodes.userNotCreated)
          }
        }
      } ) }
    } ~
    path ("foursquare" / "oauth" / Remaining) { token =>
     get { ctx => ( {
        ctx.complete(
          JsObject(Map("resultCode" -> resultCodes.success,
            "clientId" -> JsString(fqClientId),
            "callbackUri" -> JsString(fqRedirectUri)))
          .toString())
      } ) }
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
          case Some(_) => ctx.complete(JsObject("resultCode" -> resultCodes.duplicateId).toString())
          case None => ( {
            val user = new User(id)
            User.toDb(user)
            ctx.complete(JsObject("resultCode" -> resultCodes.success).toString())
          })
        }
      })} ~
      post { ctx => ( {
        User.fromDb(id) match {
          case Some(_) => ctx.complete(JsObject("resultCode" -> resultCodes.success).toString())
          case None => ( {
            ctx.complete(JsObject("resultCode" -> resultCodes.idNotFound).toString())
          })
        }
      } ) } ~
        delete { ctx => ( {
          User.fromDb(id) match {
            case Some(user) => ( {
              User.delete(user)
              ctx.complete(JsObject("resultCode" -> resultCodes.success).toString())
            } )
            case None => ctx.complete(JsObject("resultCode" -> resultCodes.idNotFound).toString())
          }
        } ) }
    }
  }
  
}