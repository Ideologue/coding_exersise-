package dtos
import play.api.libs.json._
import java.util.Date

case class ExchangeDto(date: Date, source: String, target: String, amount: Float, result: Float)

object ExchangeDto {
  implicit val formatter: Format[ExchangeDto] = Json.format[ExchangeDto]
}