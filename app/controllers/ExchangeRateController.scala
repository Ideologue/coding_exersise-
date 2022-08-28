package controllers

import javax.inject._
import models.ExchangeRateItem
import play.api.libs.json._
import play.api.mvc.{ Action, AnyContent, BaseController, ControllerComponents }
import scala.collection.mutable
import java.util.Date
import scala.io.Source
import java.nio.file.Paths

import java.io.File
import java.nio.file.{ Files, Path }
import javax.inject._

import akka.stream.IOResult
import akka.stream.scaladsl._
import akka.util.ByteString
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.streams._
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc._
import play.core.parsers.Multipart.FileInfo

import scala.concurrent.{ ExecutionContext, Future }
import java.text.SimpleDateFormat
import dtos.ExchangeDto
import dtos.MaxDto
import dtos.AvarageDto

@Singleton
class ExchangeRateController @Inject() (cc: MessagesControllerComponents)(implicit executionContext: ExecutionContext)
  extends MessagesAbstractController(cc) {

  implicit class ImplDoubleVecUtils(values: Seq[Float]) {

    def avarage = values.sum / values.length
  }

  private val logger = Logger(this.getClass)

  val exchangeRateItemList = new mutable.ListBuffer[ExchangeRateItem]()
  val formatter = new SimpleDateFormat("yyyy-MM-dd")

  type FilePartHandler[A] = FileInfo => Accumulator[ByteString, FilePart[A]]

  /**
   * Uses a custom FilePartHandler to return a type of "File" rather than
   * using Play's TemporaryFile class.  Deletion must happen explicitly on
   * completion, rather than TemporaryFile (which uses finalization to
   * delete temporary files).
   *
   * @return
   */
  private def handleFilePartAsFile: FilePartHandler[File] = {
    case FileInfo(partName, filename, contentType, _) =>
      val path: Path = Files.createTempFile("multipartBody", "tempFile")
      val fileSink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(path)
      val accumulator: Accumulator[ByteString, IOResult] = Accumulator(fileSink)
      accumulator.map {
        case IOResult(count, status) =>
          logger.info(s"count = $count, status = $status")
          FilePart(partName, filename, contentType, path.toFile)
      }
  }

  /**
   * A  operation on the temporary file that fill the in memory list of exchange rates .
   */
  private def operateOnTempFile(file: File) = {
    val size = Files.size(file.toPath)
    logger.info(s"size = ${size}")
    println(s"size = ${size}")
    val src = Source.fromFile(file.toPath().toUri())
    val iter = src.getLines().drop(1).map(_.split(","))
    iter.foreach { item =>
      val date = formatter.parse(item(0))
      exchangeRateItemList += ExchangeRateItem(
        date, if (item(1) == "N/A") 0 else item(1).toFloat, if (item(2) == "N/A") 0 else item(2).toFloat,
        if (item(3) == "N/A") 0 else item(3).toFloat, if (item(4) == "N/A") 0 else item(4).toFloat, if (item(5) == "N/A") 0 else item(5).toFloat,
        if (item(6) == "N/A") 0 else item(6).toFloat, if (item(7) == "N/A") 0 else item(7).toFloat, if (item(8) == "N/A") 0 else item(8).toFloat,
        if (item(9) == "N/A") 0 else item(9).toFloat, if (item(10) == "N/A") 0 else item(10).toFloat, if (item(11) == "N/A") 0 else item(11).toFloat,
        if (item(12) == "N/A") 0 else item(12).toFloat, if (item(13) == "N/A") 0 else item(13).toFloat, if (item(14) == "N/A") 0 else item(14).toFloat,
        if (item(15) == "N/A") 0 else item(15).toFloat, if (item(16) == "N/A") 0 else item(16).toFloat, if (item(17) == "N/A") 0 else item(17).toFloat,
        if (item(18) == "N/A") 0 else item(18).toFloat,
        if (item(19) == "N/A") 0 else item(19).toFloat, if (item(20) == "N/A") 0 else item(20).toFloat, if (item(21) == "N/A") 0 else item(21).toFloat,
        if (item(22) == "N/A") 0 else item(22).toFloat, if (item(23) == "N/A") 0 else item(23).toFloat, if (item(24) == "N/A") 0 else item(24).toFloat,
        if (item(25) == "N/A") 0 else item(25).toFloat, if (item(26) == "N/A") 0 else item(26).toFloat, if (item(27) == "N/A") 0 else item(27).toFloat,
        if (item(28) == "N/A") 0 else item(28).toFloat, if (item(29) == "N/A") 0 else item(29).toFloat, if (item(30) == "N/A") 0 else item(30).toFloat,
        if (item(31) == "N/A") 0 else item(31).toFloat, if (item(32) == "N/A") 0 else item(32).toFloat, if (item(33) == "N/A") 0 else item(33).toFloat,
        if (item(34) == "N/A") 0 else item(34).toFloat, if (item(35) == "N/A") 0 else item(35).toFloat, if (item(36) == "N/A") 0 else item(36).toFloat,
        if (item(37) == "N/A") 0 else item(37).toFloat, if (item(38) == "N/A") 0 else item(38).toFloat, if (item(39) == "N/A") 0 else item(39).toFloat,
        if (item(40) == "N/A") 0 else item(40).toFloat)
    }
    println(exchangeRateItemList.size)
    size
  }

  /*
   curl --location --request POST 'http://localhost:9000/exchange/upload' \
    --header 'Accept: application/json' \
    --form 'file=@"/path/to/eurofxref-hist.csv"'
   */
  def readRates = Action(parse.multipartFormData(handleFilePartAsFile)) { request =>

    val fileOption = request.body.file("file").map {
      case FilePart(key, filename, contentType, file, fileSize, dispositionType) =>
        logger.info(s"key = $key, filename = $filename, contentType = $contentType, file = $file, fileSize = $fileSize, dispositionType = $dispositionType")
        val data = operateOnTempFile(file)
        data
    }

    Ok(s"file size = ${fileOption.getOrElse("no file")}")
  }

  /*
  curl --location --request GET 'http://localhost:9000/exchange'
  */

  def getAll() = Action {
    if (exchangeRateItemList.isEmpty) NoContent else Ok(Json.toJson(exchangeRateItemList))
  }

  // curl --location --request GET 'http://localhost:9000/exchange/bydate?date=2015-02-10'
  def getByDate() = Action { request =>
    val dateString = request.getQueryString("date")
    val date = formatter.parse(dateString.getOrElse("2022-08-22"))
    val foundItem = exchangeRateItemList.find(_.date == date)
    foundItem match {
      case Some(item) => Ok(Json.toJson(item))
      case None       => BadRequest
    }
  }

  /*
   curl --location --request GET 'http://localhost:9000/exchange/amount?date=2015-02-10&source =usd&target=czk&amount=50'
   */
  def exchange() = Action { request =>
    val dateString = request.getQueryString("date")
    val source = request.getQueryString("source").getOrElse("usd").toLowerCase()
    val target = request.getQueryString("target").getOrElse("jpy").toLowerCase()
    val amount = request.getQueryString("amount").getOrElse("1").toFloat
    val date = formatter.parse(dateString.getOrElse("2022-08-22"))
    val foundItem = exchangeRateItemList.find(_.date == date)
    foundItem match {
      case Some(item) => {
        val sourcerate = item.getClass.getDeclaredMethod(source).invoke(item).asInstanceOf[Float]
        println(s"Source Rate is $sourcerate")
        val targetRate = item.getClass.getDeclaredMethod(target).invoke(item).asInstanceOf[Float]
        println(s"Target Rate is $targetRate")
        val targetAmount = amount * sourcerate * targetRate

        Ok(Json.toJson(ExchangeDto(date, source, target, amount, targetAmount)))
      }
      case None => NotFound
    }
  }
  /*
  curl --location --request GET 'http://localhost:9000/exchange/max?startDate=2014-02-10&endDate=2015-02-10&currency=usd'
 */
  def maxRate() = Action { request =>
    val startDateString = request.getQueryString("startDate").getOrElse("2022-07-22")
    val startDate = formatter.parse(startDateString)

    val endDateString = request.getQueryString("endDate").getOrElse("2022-08-22")
    val endDate = formatter.parse(endDateString)

    val currency = request.getQueryString("currency").getOrElse("usd").toLowerCase()
    val foundItems = exchangeRateItemList.filter { item => item.date.compareTo(startDate) >= 0 && item.date.compareTo(startDate) <= 0 }
    if (foundItems.isEmpty) NotFound else {
      val i = foundItems.maxBy { f => f.getClass.getDeclaredMethod(currency).invoke(f).asInstanceOf[Float] }
      Ok(Json.toJson(MaxDto(i.getClass.getDeclaredMethod(currency).invoke(i).asInstanceOf[Float])))
    }
  }

  /*
   curl --location --request GET 'http://localhost:9000/exchange/avarage?startDate=2014-02-10&endDate=2015-02-10&currency=usd'
   */

  def avarageRate() = Action { request =>
    val startDateString = request.getQueryString("startDate").getOrElse("2022-07-22")
    val startDate = formatter.parse(startDateString)

    val endDateString = request.getQueryString("endDate").getOrElse("2022-08-22")
    val endDate = formatter.parse(endDateString)

    val currency = request.getQueryString("currency").getOrElse("usd").toLowerCase()
    val foundItems = exchangeRateItemList.filter { item => item.date.compareTo(startDate) >= 0 && item.date.compareTo(startDate) <= 0 }
    if (foundItems.isEmpty) NotFound else {
      val i = foundItems.map { f => f.getClass.getDeclaredMethod(currency).invoke(f).asInstanceOf[Float] }.avarage

      Ok(Json.toJson(AvarageDto(i)))

    }
  }

}