package com.calistropo.pipes

import com.calistropo.models.{FilledTiles, VehicleEvent}
import org.apache.spark.streaming.{Duration, Seconds}
import org.apache.spark.streaming.dstream.DStream

import scala.compat.Platform

object CalcFilledTiles {
  def apply(batchDuration: Duration)(stream: DStream[VehicleEvent]): DStream[FilledTiles] = {


    stream.map(e => Set(e.quadKey)).reduceByWindow(_ ++ _, batchDuration*2, batchDuration)
      .map {
        set =>
          val tiles = FilledTiles(set = set: Set[String],
            timestamp = Platform.currentTime: Long)
          println(tiles)
          tiles
      }

  }
}
