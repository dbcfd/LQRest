package com.webwino.rest

import org.specs2.mutable._
import cc.spray._
import test._
import http._
import HttpMethods._
import StatusCodes._
import cc.spray.json._
import cc.spray.typeconversion._
import org.specs2.specification.{Given, When,  Then}
import org.specs2.matcher._
import scala.util.matching.Regex

class RestServiceSpec extends Specification with SprayTest with Rest with SprayJsonSupport {
  val resultParser = new Regex(""".*\"resultCode\"[.]*?:[.]*?(\d+).*""")
  class ResultMatcher(val resultCode:JsNumber) extends Matcher[Either[DeserializationError, String]] {
    def apply[S <: Either[DeserializationError,String]](s: Expectable[S]) = { s.value match {
      case e:Right[_,_] => {
        val str:String = e.right.get
        val resultParser(code) = str
        result(resultCode.toString().equals(code),
          "Response " + str + " with resultCode " + code + " is " + resultCode,
          "Response " + str + " with resultCode " + code + " is not " + resultCode,
          s)
      }
    }}
  }
  "The RestService" should {
    "return a greeting for GET requests to the test path" in {
      testService(HttpRequest(GET, "/test")) {
        restService
      }.response.content.as[String] mustEqual Right("Say hello to Spray!")
    }
    "return a success response for GET requests to the api/users/testid path" in {
      testService(HttpRequest(GET, "/api/users/testid")) {
        restService
      }.response.content.as[String] must new ResultMatcher(resultCodes.success)
    }
    "return a id not found response for GET requests to the api/users/badid path" in {
      testService(HttpRequest(GET, "/api/users/badid")) {
        restService
      }.response.content.as[String] must new ResultMatcher(resultCodes.idNotFound)
    }
    "return a duplicate id response for PUT requests to the api/users/testid path" in {
      testService(HttpRequest(PUT, "/api/users/testid")) {
        restService
      }.response.content.as[String] must new ResultMatcher(resultCodes.duplicateId)
    }
    "return a success response for PUT requests to the api/users/dummyid path" in {
      testService(HttpRequest(PUT, "/api/users/dummyid")) {
        restService
      }.response.content.as[String] must new ResultMatcher(resultCodes.success)
    }
    "return a success response for DELETE requests to the api/users/dummyid path" in {
      testService(HttpRequest(DELETE, "/api/users/dummyid")) {
        restService
      }.response.content.as[String] must new ResultMatcher(resultCodes.success)
    }
    "leave GET requests to other paths unhandled" in {
      testService(HttpRequest(GET, "/kermit")) {
        restService
      }.handled must beFalse
    }
  }
  
}