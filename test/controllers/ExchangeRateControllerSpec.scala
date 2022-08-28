package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

import java.io._
import java.nio.file.Files

import akka.stream.scaladsl._
import akka.util.ByteString
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient
import play.api.mvc._

import models.ExchangeRateItem
import play.api.libs.json.Json
import dtos.ExchangeDto
import dtos.MaxDto
import dtos.AvarageDto
import play.core.routing.ReverseRouteContext
import java.nio.file.Files

class ExchangeRateControllerSpec extends PlaySpec with GuiceOneServerPerSuite with Injecting {
  
  "ExchangeRateController" must {
    "upload a file successfully" in {
      val file = new File("/home/ibrahim/coding-exercise/eurofxref-hist.csv")

      val url = s"http://localhost:${port}/exchange/upload"
      val responseFuture = inject[WSClient].url(url).post(postSource(file))
      val response = await(responseFuture)
      response.status mustBe OK
      response.body contains "file size ="

    }

  }
  def postSource(tmpFile: File): Source[MultipartFormData.Part[Source[ByteString, _]], _] = {
    import play.api.mvc.MultipartFormData._
    Source(FilePart("name", "hello.csv", Option("text/plain"),
      FileIO.fromPath(tmpFile.toPath)) :: DataPart("key", "value") :: List())
  }

  "ExchangeRateController GET /exchange/bydate" should {

    "get the exchange rate item  from a new instance of controller" in {

      //val controller = new ExchangeRateController(stubMessagesControllerComponents())

      //val resp = controller.getByDate().apply(FakeRequest(GET, s"http://localhost:${port}/exchange/bydate?date=2015-02-10"))
      val responseFuture = inject[WSClient].url(s"http://localhost:${port}/exchange/bydate?date=2015-02-10").get()
      val resp = await(responseFuture)
      resp.status mustBe OK
      resp.contentType mustBe Some("application/json")
      val Some(item) = Json.parse(resp.body).asOpt[ExchangeRateItem]
      item.usd mustBe 1.129699945449829
    }

    "get the exchange rate item  from the application" in {
      val controller = inject[ExchangeRateController]
      println(s"the size of the list is ${controller.exchangeRateItemList.size}")
      val resp = controller.getByDate().apply(FakeRequest(GET, s"http://localhost:${port}/exchange/bydate?date=2015-02-10"))

      status(resp) mustBe OK
      contentType(resp) mustBe Some("application/json")
      val Some(item) = Json.parse(contentAsString(resp)).asOpt[ExchangeRateItem]
      item.usd mustBe 1.129699945449829
    }

    "get the exchange rate  from the router" in {
      val request = FakeRequest(GET, s"http://localhost:${port}/exchange/bydate?date=2015-02-10")
      val resp = route(app, request).get

      status(resp) mustBe OK
      contentType(resp) mustBe Some("application/json")
      val Some(item) = Json.parse(contentAsString(resp)).asOpt[ExchangeRateItem]
      item.usd mustBe 1.129699945449829
    }
  }

  "ExchangeRateController GET /exchange/amount" should {

    "get the exchanged amount from a new instance of controller" in {
      val controller = new ExchangeRateController(stubMessagesControllerComponents())
      val resp = controller.exchange().apply(FakeRequest(GET, "/exchage/amount??date=2015-02-10&source =usd&target=czk&amount=50"))

      status(resp) mustBe OK
      contentType(resp) mustBe Some("application/json")
      val Some(item) = Json.parse(contentAsString(resp)).asOpt[ExchangeDto]
      item.result mustBe 1.129699945449829
    }

    "get the exchanged amount  from the application" in {
      val controller = inject[ExchangeRateController]
      val resp = controller.exchange().apply(FakeRequest(GET, "/exchage/amount??date=2015-02-10&source =usd&target=czk&amount=50"))

      status(resp) mustBe OK
      contentType(resp) mustBe Some("application/json")
      val Some(item) = Json.parse(contentAsString(resp)).asOpt[ExchangeDto]
      item.result mustBe 1.129699945449829
    }

    "get the exchanged amount from the router" in {
      val request = FakeRequest(GET, "/exchage/amount??date=2015-02-10&source =usd&target=czk&amount=50")
      val resp = route(app, request).get

      status(resp) mustBe OK
      contentType(resp) mustBe Some("application/json")
      val Some(item) = Json.parse(contentAsString(resp)).asOpt[ExchangeDto]
      item.result mustBe 1.129699945449829
    }
  }

  "ExchangeRateController GET /exchange/max" should {

    "get the max exchange rate item  from a new instance of controller" in {
      val controller = new ExchangeRateController(stubMessagesControllerComponents())
      val resp = controller.maxRate().apply(FakeRequest(GET, "/exchage/max?startDate=2014-02-10&endDate=2015-02-10&currency=usd"))

      status(resp) mustBe OK
      contentType(resp) mustBe Some("application/json")
      val Some(item) = Json.parse(contentAsString(resp)).asOpt[MaxDto]
      item.max mustBe 1.363800048828125
    }

    "get the max exchange rate item  from the application" in {
      val controller = inject[ExchangeRateController]
      val resp = controller.maxRate().apply(FakeRequest(GET, "/exchage/max?startDate=2014-02-10&endDate=2015-02-10&currency=usd"))

      status(resp) mustBe OK
      contentType(resp) mustBe Some("application/json")
      val Some(item) = Json.parse(contentAsString(resp)).asOpt[MaxDto]
      item.max mustBe 1.363800048828125
    }

    "get the max exchange rate item from the router" in {
      val request = FakeRequest(GET, "/exchage/max?startDate=2014-02-10&endDate=2015-02-10&currency=usd")
      val resp = route(app, request).get

      status(resp) mustBe OK
      contentType(resp) mustBe Some("application/json")
      val Some(item) = Json.parse(contentAsString(resp)).asOpt[MaxDto]
      item.max mustBe 1.363800048828125
    }
  }

  "ExchangeRateController GET /exchange/avarage" should {

    "get the avarage exchange rate item  from a new instance of controller" in {
      val controller = new ExchangeRateController(stubMessagesControllerComponents())
      val resp = controller.avarageRate().apply(FakeRequest(GET, "/exchage/max?startDate=2014-02-10&endDate=2015-02-10&currency=usd"))

      status(resp) mustBe OK
      contentType(resp) mustBe Some("application/json")
      val Some(item) = Json.parse(contentAsString(resp)).asOpt[AvarageDto]
      item.avarage mustBe 1.363800048828125
    }

    "get the avarage exchange rate item  from the application" in {
      val controller = inject[ExchangeRateController]
      val resp = controller.avarageRate().apply(FakeRequest(GET, "/exchage/max?startDate=2014-02-10&endDate=2015-02-10&currency=usd"))

      status(resp) mustBe OK
      contentType(resp) mustBe Some("application/json")
      val Some(item) = Json.parse(contentAsString(resp)).asOpt[AvarageDto]
      item.avarage mustBe 1.363800048828125
    }

    "get the avarge exchange rate item from the router" in {
      val request = FakeRequest(GET, "/exchage/max?startDate=2014-02-10&endDate=2015-02-10&currency=usd")
      val resp = route(app, request).get

      status(resp) mustBe OK
      contentType(resp) mustBe Some("application/json")
      val Some(item) = Json.parse(contentAsString(resp)).asOpt[AvarageDto]
      item.avarage mustBe 1.363800048828125
    }
  }

}
