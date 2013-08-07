package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

/**
 */
class ApplicationSpec extends Specification {
  
  "Application" should {

    "basic login" in {
      running(FakeApplication()) {
        val res = route(FakeRequest(POST, "/login/basic")).get
        status(res) must equalTo(OK)
      }
    }
    
/*    "send 404 on a bad request" in {
      running(FakeApplication()) {
        route(FakeRequest(GET, "/boum")) must beNone        
      }
    }
    
    "render the index page" in {
      running(FakeApplication()) {
        val home = route(FakeRequest(GET, "/")).get

        status(home) must equalTo(OK)
        contentType(home) must beSome.which(_ == "text/html")
        contentAsString(home) must contain ("Your new application is ready.")
      }
    }*/
  }
}