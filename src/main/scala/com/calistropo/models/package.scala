package com.calistropo

import play.api.libs.json.{JsValue, Reads, Writes}

package object models {
  private[models] def lazyReads[T](r: => Reads[T]): Reads[T] = new Reads[T] with Serializable {
    @transient private lazy val lr = r

    def reads(json: JsValue) = lr.reads(json)
  }

  private[models] def lazyWrites[T](r: => Writes[T]): Writes[T] = new Writes[T] with Serializable {
    @transient private lazy val lr = r

    def writes(json: T) = lr.writes(json)
  }
}
