package com.webwino.rest

import cc.spray._
import cc.spray.json._
import directives.Remaining

trait Rest extends Directives {
  
  val restService = {
    path("test") {
      get { _.complete("Say hello to Spray!") }
    } ~
    path("api" / "users" / Remaining) { id =>
      get { ctx => ( {
        val response = JsObject("result" -> JsString("this is the result"))
        ctx.complete(response.toString())
      } ) } ~
      post { ctx => ( {
        val response = JsObject("result" -> JsString("sucessful post"))
        ctx.complete(response.toString())
      } ) }
    }
  }
  
}