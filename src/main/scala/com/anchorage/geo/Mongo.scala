package com.anchorage.geo


import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api.{MongoConnection, MongoDriver}
import reactivemongo.bson.{BSONWriter, BSONDateTime, BSONReader, BSONDocument}
import spray.json.RootJsonFormat
import sprest.models.UniqueSelector
import sprest.models.UUIDStringId
import sprest.reactivemongo.BsonProtocol
import sprest.reactivemongo.ReactiveMongoPersistence
import sprest.reactivemongo.typemappers.NormalizedIdTransformer
import sprest.reactivemongo.typemappers.SprayJsonTypeMapper
import scala.util.{Try, Properties}
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
import com.anchorage.geo.api.DefaultJsonFormats
import com.anchorage.geo.model.Device

trait Mongo extends ReactiveMongoPersistence with DefaultJsonFormats {
  import Akka.actorSystem

  //Set up Mongo connection and DB
  private lazy val config = ConfigFactory.load()
  private lazy val uri = config.getString("com.anchorage.mongodb.uri")
  private lazy val dbName =  uri.substring(uri.lastIndexOf("/") + 1)  //DO THIS BETTER in scala

  private val driver = new MongoDriver(actorSystem)

  val connection: Try[MongoConnection] =
    MongoConnection.parseURI(uri).map { parsedUri =>
      driver.connection(parsedUri)
    }
  private val db = connection.get.db(dbName)
  //End Config set up

  // Json mapping to / from BSON - in this case we want "_id" from BSON to be
  // mapped to "id" in JSON in all cases
//  implicit object JsonTypeMapper extends SprayJsonTypeMapper with NormalizedIdTransformer

  abstract class UnsecuredDAO[M <: sprest.models.Model[String]](collName: String)(implicit jsformat: RootJsonFormat[M])
    extends CollectionDAO[M, String](db(collName)) {

    case class Selector(id: String) extends UniqueSelector[M, String]

    override def generateSelector(id: String) = Selector(id)
    override protected def addImpl(m: M)(implicit ec: ExecutionContext) = doAdd(m)
    override protected def updateImpl(m: M)(implicit ec: ExecutionContext) = doUpdate(m)
    override def remove(selector: Selector)(implicit ec: ExecutionContext) = uncheckedRemoveById(selector.id)

    protected val collection: BSONCollection = db(collName)
    def removeAll()(implicit ec: ExecutionContext) = collection.remove(BSONDocument.empty)
    def findAll()(implicit ec: ExecutionContext) = find(BSONDocument.empty)
  }

  // MongoDB collections:

  object DeviceDetails extends UnsecuredDAO[Device]("devices") with UUIDStringId {
    def findByDeviceId(deviceId: String)(implicit ec: ExecutionContext) = find(BSONDocument("deviceId" → deviceId))
  }

  implicit object DatetimeReader extends BSONReader[BSONDateTime, DateTime]{
    def read(bson: BSONDateTime): DateTime = new DateTime(bson.value)
  }

  implicit object DatetimeWriter extends BSONWriter[DateTime, BSONDateTime]{
    def write(t: DateTime): BSONDateTime = BSONDateTime(t.getMillis)
  }
}
object Mongo extends Mongo
