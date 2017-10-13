package com.barracuda.pipes

import java.util

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.spark.streaming.dstream.DStream
import play.api.libs.json.{Json, Writes}

object Send2Kafka {


  def apply[T:Writes](topic:String)(stream:DStream[T])={

    stream.map{
      x=>
        new ProducerRecord[String, String](topic,null,Json.toJson(x).toString())
    }.foreachRDD{
      rdd=>
        rdd.foreachPartition{
          partition=>
            val props = new util.HashMap[String, Object]()
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
              "org.apache.kafka.common.serialization.StringSerializer")
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
              "org.apache.kafka.common.serialization.StringSerializer")
            val producer = new KafkaProducer[String,String](props)
            partition.foreach{
              record=>
                producer.send(record)
            }

        }
    }



  }
}
