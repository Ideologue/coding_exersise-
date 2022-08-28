package dtos
import play.api.libs.json._

case class MaxDto(max: Float)

object MaxDto {

  implicit val formatter: Format[MaxDto] = Json.format[MaxDto]
}