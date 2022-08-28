package dtos

import play.api.libs.json._

case class AvarageDto(avarage: Float)

object AvarageDto {
  implicit val formatter: Format[AvarageDto] = Json.format[AvarageDto]
}