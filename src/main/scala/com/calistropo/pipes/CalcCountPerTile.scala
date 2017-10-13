package com.calistropo.pipes

import com.calistropo.models.{CountPerTile, VehicleEvent}
import org.apache.spark.streaming.{Duration, Seconds}
import org.apache.spark.streaming.dstream.DStream

import scala.compat.Platform

object CalcCountPerTile {
  def apply(batchDuration: Duration)(stream: DStream[VehicleEvent]) = {

    stream.map(e => e.quadKey -> e.id).groupByKeyAndWindow(batchDuration * 10, batchDuration)
      .map {
        case (quadKey, vIds) =>
          println("count per tile")
          CountPerTile(tile = quadKey,
            value = vIds.toSet.size: Long, timestamp = Platform.currentTime: Long)
      }

  }
}
