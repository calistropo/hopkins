package com.calistropo.models

import com.datastax.spark.connector.mapper.DefaultColumnMapper
import play.api.libs.json.{JsValue, Json, Reads}

case class VehicleRawEvent(
                            id: String,
                            run_id: Option[String],
                            latitude: Double,
                            longitude: Double,
                            heading: Double,
                            seconds_since_report: Int,
                            predictable: Boolean,
                            route_id: String
                          )
object VehicleRawEvent {
  //Workaround so that the reads is serializable

  implicit def reads = lazyReads(Json.reads[VehicleRawEvent])

  implicit def writes = Json.writes[VehicleRawEvent]
}
case class VehicleEvent(
                         id: String,
                         runId: Option[String],
                         latitude: Double,
                         longitude: Double,
                         heading: Double,
                         //This doesn't make sense
                         //seconds_since_report: Int,
                         predictable: Boolean,
                         routeId: String,
                         timestamp: Long,
                         quadKey: String
                       )

object VehicleEvent{
  implicit def reads = lazyReads(Json.reads[VehicleEvent])

  implicit def writes = lazyWrites(Json.writes[VehicleEvent])
  implicit def mapper = new DefaultColumnMapper[VehicleEvent](Map(
    "runId"->"run_id",
    "routeId"->"route_id",
    "quadKey"->"quad_key"
  ))
}


