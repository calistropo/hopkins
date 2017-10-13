package com.calistropo.pipes

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import play.api.libs.json.{Json, Reads}

import scala.reflect.ClassTag

object PullFromKafka {


  def apply[T:Reads:ClassTag](config: KafkaConfig): DStream[T] = {



    KafkaUtils.createDirectStream[String, String](
      config.streamingContext,
      PreferConsistent,
      Subscribe[String, String](Array(config.topic), Map[String, Object](
        "bootstrap.servers" -> config.bootstrapServers.mkString(","),
        "key.deserializer" -> classOf[StringDeserializer],
        "value.deserializer" -> classOf[StringDeserializer],
        "group.id" -> config.groupId,
        "auto.offset.reset" -> "latest",
        "enable.auto.commit" -> (false: java.lang.Boolean)
      )))
      .map {
        record =>
          implicitly[Reads[T]].reads(Json.parse(record.value())).get
      }
  }


}

case class KafkaConfig(streamingContext: StreamingContext,
                       topic: String,
                       groupId: String,
                       bootstrapServers: Set[String]
                      )