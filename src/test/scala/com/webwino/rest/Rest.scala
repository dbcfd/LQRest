package com.webwino.rest

import org.specs2.mutable._
import cc.spray._
import test._
import http._
import HttpMethods._
import StatusCodes._

class RestServiceSpec extends Specification with SprayTest with Rest {
  
  "The RestService" should {
    "return a greeting for GET requests to the test path" in {
      testService(HttpRequest(GET, "/test")) {
        restService
      }.response.content.as[String] mustEqual Right("Say hello to Spray!")
    }
    "return a response for GET requests to the api/users/:id path" in {
      testService(HttpRequest(GET, "/api/users/dummyid")) {
        restService
      }.response.content.as[String] mustEqual Right("some response")
    }
    "return a response for POST requests to the api/users/:id path" in {
      testService(HttpRequest(POST, "/api/users/dummyid")) {
        restService
      }.response.content.as[String] mustEqual Right("some response")
    }
    "leave GET requests to other paths unhandled" in {
      testService(HttpRequest(GET, "/kermit")) {
        restService
      }.handled must beFalse
    }
  }
  
}