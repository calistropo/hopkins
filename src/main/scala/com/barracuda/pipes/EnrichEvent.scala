package com.barracuda.pipes


import com.barracuda.models.{VehicleEvent, VehicleRawEvent}
import com.barracuda.util.TileCalc
import org.apache.spark.streaming.dstream.DStream

import scala.compat.Platform

object EnrichEvent extends ((DStream[VehicleRawEvent]) => DStream[VehicleEvent]) {


  private def quadKey(vehicle: VehicleRawEvent): String = {

    TileCalc.convertLatLongToQuadKey(vehicle.latitude, vehicle.longitude, 23)
  }

  override def apply(stream: DStream[VehicleRawEvent]) = {
    stream.map {
      raw =>
        VehicleEvent(id = raw.id: String,
          runId = raw.run_id: Option[String],
          latitude = raw.latitude: Double,
          longitude = raw.longitude: Double,
          heading = raw.heading: Double,
          //This doesn't make sense
          //seconds_since_report: Int,
          predictable = raw.predictable: Boolean,
          routeId = raw.route_id: String,
          timestamp = Platform.currentTime: Long,
          quadKey = quadKey(raw): String)
    }
  }
}
