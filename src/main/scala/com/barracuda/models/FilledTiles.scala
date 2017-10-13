package com.barracuda.models

import play.api.libs.json.Json

case class FilledTiles(set:Set[String],
                       timestamp:Long)

object FilledTiles{
  implicit def reads=lazyReads(Json.reads[FilledTiles])
  implicit def writes=lazyWrites(Json.writes[FilledTiles])
}