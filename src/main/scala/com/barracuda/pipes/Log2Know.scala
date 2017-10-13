package com.barracuda.pipes

import org.apache.spark.streaming.dstream.DStream

import scala.compat.Platform
import scala.reflect.ClassTag

object Log2Know {
  def apply[T:ClassTag](name: String)(stream: DStream[T]): DStream[T] = {
    stream.transform {
      rdd =>
        rdd.foreachPartition{part=>
          println(s"$name@${Platform.currentTime}:${part.length}")
        }
        rdd
    }
  }
}
