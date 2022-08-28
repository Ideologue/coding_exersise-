package models

import java.util.Date
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class ExchangeRateItem(date: Date, usd: Float, jpy: Float, bgn: Float, cyp: Float, czk: Float, dkk: Float, eek: Float, gbp: Float, huf: Float,
                            ltl: Float, lvl: Float, mtl: Float, pln: Float, ron: Float, sek: Float, sit: Float, skk: Float, chf: Float,
                            isk: Float, nok: Float, hrk: Float, rub: Float, trl: Float, TRY: Float, aud: Float, brl: Float, cad: Float, cny: Float,
                            hkd: Float, idr: Float, ils: Float, inr: Float, krw: Float, mxn: Float, myr: Float,
                            nzd: Float, php: Float, sgd: Float, thp: Float, zar: Float)

object ExchangeRateItem {
  val fields1to10: OFormat[(Date, Float, Float, Float, Float, Float, Float, Float, Float)] = (
    (__ \ "date").format[Date] and
    (__ \ "usd").format[Float] and
    (__ \ "jpy").format[Float] and
    (__ \ "bgn").format[Float] and
    (__ \ "cyp").format[Float] and
    (__ \ "czk").format[Float] and
    (__ \ "dkk").format[Float] and
    (__ \ "eek").format[Float] and
    (__ \ "gbp").format[Float]).tupled

  val fields11to20: OFormat[(Float, Float, Float, Float, Float, Float, Float, Float, Float, Float)] = (
    (__ \ "huf").format[Float] and
    (__ \ "ltl").format[Float] and
    (__ \ "lvl").format[Float] and
    (__ \ "mtl").format[Float] and
    (__ \ "pln").format[Float] and
    (__ \ "ron").format[Float] and
    (__ \ "sek").format[Float] and
    (__ \ "sit").format[Float] and
    (__ \ "skk").format[Float] and
    (__ \ "chf").format[Float]).tupled

  val fields21to30: OFormat[(Float, Float, Float, Float, Float, Float, Float, Float, Float, Float)] = (
    (__ \ "isk").format[Float] and
    (__ \ "nok").format[Float] and
    (__ \ "hrk").format[Float] and
    (__ \ "rub").format[Float] and
    (__ \ "trl").format[Float] and
    (__ \ "try").format[Float] and
    (__ \ "aud").format[Float] and
    (__ \ "brl").format[Float] and
    (__ \ "cad").format[Float] and
    (__ \ "cny").format[Float]).tupled

  val fields31toEnd: OFormat[(Float, Float, Float, Float, Float, Float, Float, Float, Float, Float, Float, Float)] = (
    (__ \ "hkd").format[Float] and
    (__ \ "idr").format[Float] and
    (__ \ "ils").format[Float] and
    (__ \ "inr").format[Float] and
    (__ \ "krw").format[Float] and
    (__ \ "mxn").format[Float] and
    (__ \ "myr").format[Float] and
    (__ \ "nzd").format[Float] and
    (__ \ "php").format[Float] and
    (__ \ "sgd").format[Float] and

    (__ \ "thp").format[Float] and
    (__ \ "zar").format[Float]).tupled

  implicit val exchangeRateItemFormat: Format[ExchangeRateItem] = (fields1to10 ~ fields11to20 ~ fields21to30 ~ fields31toEnd)({
    case ((a, b, c, d, e, f, g, h, i), (j, k, l, m, n, o, p, q, r, s), (t, u, v, w, x, y, z, a1, b1, c1), (d1, e1, f1, g1, h1, i1, j1, k1, l1, m1, n1, o1)) =>
      new ExchangeRateItem(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z, a1, b1, c1, d1, e1, f1, g1, h1, i1, j1, k1, l1, m1, n1, o1)
  }, (exchangeRateItem: ExchangeRateItem) => ((
    exchangeRateItem.date,
    exchangeRateItem.usd,
    exchangeRateItem.jpy,
    exchangeRateItem.bgn,
    exchangeRateItem.cyp,
    exchangeRateItem.czk,
    exchangeRateItem.dkk,
    exchangeRateItem.eek,
    exchangeRateItem.gbp), (
      exchangeRateItem.huf,
      exchangeRateItem.ltl,
      exchangeRateItem.lvl,
      exchangeRateItem.mtl,
      exchangeRateItem.pln,
      exchangeRateItem.ron,
      exchangeRateItem.sek,
      exchangeRateItem.sit,
      exchangeRateItem.skk,
      exchangeRateItem.chf), (
        exchangeRateItem.isk,
        exchangeRateItem.nok,
        exchangeRateItem.hrk,
        exchangeRateItem.rub,
        exchangeRateItem.trl,
        exchangeRateItem.TRY,
        exchangeRateItem.aud,
        exchangeRateItem.brl,
        exchangeRateItem.cad,
        exchangeRateItem.cny), (
          exchangeRateItem.hkd,
          exchangeRateItem.idr,
          exchangeRateItem.ils,
          exchangeRateItem.inr,
          exchangeRateItem.krw,
          exchangeRateItem.mxn,
          exchangeRateItem.myr,
          exchangeRateItem.nzd,
          exchangeRateItem.php,
          exchangeRateItem.sgd,
          exchangeRateItem.thp,
          exchangeRateItem.zar)))

}