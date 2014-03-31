package com.anchorage.geo.api

import spray.json._
import spray.httpx.SprayJsonSupport
import spray.httpx.marshalling.{CollectingMarshallingContext, Marshaller, MetaMarshallers}
import scala.reflect.ClassTag
import com.anchorage.geo.api.ErrorResponseException
import java.util.UUID
import com.anchorage.geo.api.ErrorResponseException
import spray.http.StatusCode
import sprest.reactivemongo.typemappers.{NormalizedIdTransformer, SprayJsonTypeMapper}
import org.joda.time.DateTime
import com.anchorage.geo.{MappingDetail, Location}
import com.anchorage.geo.model.Device

/**
 * Contains useful JSON formats: ``j.u.Date``, ``j.u.UUID`` and others; it is useful
 * when creating traits that contain the ``JsonReader`` and ``JsonWriter`` instances
 * for types that contain ``Date``s, ``UUID``s and such like.
 */
trait DefaultJsonFormats extends DefaultJsonProtocol with SprayJsonSupport with MetaMarshallers {

  implicit object JsonTypeMapper extends SprayJsonTypeMapper with NormalizedIdTransformer

  implicit val locationJsonFormat = jsonFormat2(Location.apply _)
  implicit val mappingDetailJsonFormat = jsonFormat2(MappingDetail.apply _)
  implicit val deviceJsonFormat = jsonFormat7(Device.apply _)

  /**
   * Computes ``RootJsonFormat`` for type ``A`` if ``A`` is object
   */
  def jsonObjectFormat[A : ClassTag]: RootJsonFormat[A] = new RootJsonFormat[A] {
    val ct = implicitly[ClassTag[A]]
    def write(obj: A): JsValue = JsObject("value" -> JsString(ct.runtimeClass.getSimpleName))
    def read(json: JsValue): A = ct.runtimeClass.newInstance().asInstanceOf[A]
  }

  /**
   * Instance of the ``RootJsonFormat`` for the ``j.u.UUID``
   */
  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit object DateTimeJsonFormat extends RootJsonFormat[DateTime] {
    def write(date: DateTime) = JsString(date.toString())

    def read(value: JsValue) = value match {
      case JsString(date) => new DateTime(date)
      case _ => throw new DeserializationException("DateTime expected")
    }
  }

  /**
   * Type alias for function that converts ``A`` to some ``StatusCode``
   * @tparam A the type of the input values
   */
  type ErrorSelector[A] = A => StatusCode

  /**
   * Marshals instances of ``Either[A, B]`` into appropriate HTTP responses by marshalling the values
   * in the left or right projections; and by selecting the appropriate HTTP status code for the
   * values in the left projection.
   *
   * @param ma marshaller for the left projection
   * @param mb marshaller for the right projection
   * @param esa the selector converting the left projection to HTTP status code
   * @tparam A the left projection
   * @tparam B the right projection
   * @return marshaller
   */
  implicit def errorSelectingEitherMarshaller[A, B](implicit ma: Marshaller[A], mb: Marshaller[B], esa: ErrorSelector[A]): Marshaller[Either[A, B]] =
    Marshaller[Either[A, B]] { (value, ctx) =>
      value match {
        case Left(a) =>
          val mc = new CollectingMarshallingContext()
          ma(a, mc)
          ctx.handleError(ErrorResponseException(esa(a), mc.entity))
        case Right(b) =>
          mb(b, ctx)
      }
    }

}
