package com.webwino.rest

import org.specs2.mutable._
import cc.spray._
import test._
import http._
import HttpMethods._
import StatusCodes._
import cc.spray.json._

class RestServiceSpec extends Specification with SprayTest with Rest {
  def jsMatcher:Matcher[Either[DeserializationError,String]]
  
  "The RestService" should {
    "return a greeting for GET requests to the test path" in {
      testService(HttpRequest(GET, "/test")) {
        restService
      }.response.content.as[String] mustEqual Right("Say hello to Spray!")
    }
    "return a success response for GET requests to the api/users/testid path" in {
      testService(HttpRequest(GET, "/api/users/testid")) {
        restService
      }.response.content.as[String] must /("resultCode" -> resultCodes.success.value)
    }
    "return a id not found response for GET requests to the api/users/badid path" in {
      testService(HttpRequest(GET, "/api/users/badid")) {
        restService
      }.response.content.as[String] mustEqual mustEqual Right(Rest.resultCodes.idNotFound)
    }
    "return a duplicate id response for PUT requests to the api/users/testid path" in {
      testService(HttpRequest(PUT, "/api/users/testid")) {
        restService
      }.response.content.as[String] mustEqual mustEqual Right(Rest.resultCodes.duplicateId)
    }
    "return a success response for PUT requests to the api/users/dummyid path" in {
      testService(HttpRequest(PUT, "/api/users/dummyid")) {
        restService
      }.response.content.as[String] mustEqual mustEqual Right(Rest.resultCodes.success)
    }
    "return a success response for DELETE requests to the api/users/dummyid path" in {
      testService(HttpRequest(DELETE, "/api/users/dummyid")) {
        restService
      }.response.content.as[JsObject]("resultCode") mustEqual Right(Rest.resultCodes.success)
    }
    "leave GET requests to other paths unhandled" in {
      testService(HttpRequest(GET, "/kermit")) {
        restService
      }.handled must beFalse
    }
  }
  
}