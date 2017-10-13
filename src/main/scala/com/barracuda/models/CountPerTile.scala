package com.barracuda.models

import com.datastax.spark.connector.mapper.{ColumnMapper, DefaultColumnMapper}
import play.api.libs.json.Json

case class CountPerTile(tile: String,
                        value: Long,
                        timestamp: Long)


object CountPerTile {
  implicit def reads = lazyReads(Json.reads[CountPerTile])

  implicit def writes = lazyWrites(Json.writes[CountPerTile])

  implicit def mapper: ColumnMapper[CountPerTile] = new DefaultColumnMapper[CountPerTile]

}