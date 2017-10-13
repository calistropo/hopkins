package com.barracuda

import com.barracuda.models.{CountPerTile, FilledTiles, VehicleEvent, VehicleRawEvent}
import com.barracuda.pipes._
import com.barracuda.util.Idioms._
import com.datastax.spark.connector.cql.CassandraConnector
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Main {


  def lazyInit[T](block: => T) = {}


  def main(args: Array[String]) = {
    val conf = new SparkConf().setAppName(s"hopkins_${args.head}")
      .set("spark.cassandra.connection.host", "localhost")
      .set("spark.executor.memory", "1g")

    val batchDuration = Seconds(2)
    val ssc = new StreamingContext(conf, batchDuration)
    //I don't know why this is incorrect according to intellij, but according to scalac is good
    implicit val connector = CassandraConnector(conf)

    args.head match {
      case name@"enrich" =>
        //Enriches the raw events and adds tile id (quadkey) and timestamp because
        //seconds since report is not working correctly, so f*ck it
        //Eventually I send it back to kafka in another topic
        PullFromKafka[VehicleRawEvent](KafkaConfig(streamingContext = ssc,
          topic = "raw_vehicle_events": String,
          groupId = name: String,
          bootstrapServers = Set("localhost:9092"): Set[String])
        ) |> Log2Know(name) |> EnrichEvent |> Send2Kafka[VehicleEvent]("vehicle_events")
      case name@"save_vehicle_events" =>
        //
        //Saves in cassandra the filled tiles report every once in a while
        //Deprecated, I'll just use the count
        PullFromKafka[VehicleEvent](KafkaConfig(streamingContext = ssc,
          topic = "vehicle_events": String,
          groupId = name: String,
          bootstrapServers = Set("localhost:9092"): Set[String])
        ) |> Log2Know(name) |> Send2Cassandra[VehicleEvent]("audi", "vehicle_events")
      case name@"count_per_tile" =>
        //Calculates count of vehicles per tile and sends the report back to kafka
        PullFromKafka[VehicleEvent](KafkaConfig(streamingContext = ssc,
          topic = "vehicles": String,
          groupId = name: String,
          bootstrapServers = Set("localhost:9092"): Set[String])
        ) |> Log2Know(name) |> CalcCountPerTile(batchDuration) |> Send2Kafka[CountPerTile]("counts_per_tile")
      case name@"save_counts_per_tile" =>

        //Saves in cassandra the count per tile report every once in a while
        PullFromKafka[CountPerTile](KafkaConfig(streamingContext = ssc,
          topic = "counts_per_tile": String,
          groupId = name: String,
          bootstrapServers = Set("localhost:9092"): Set[String])
        ) |> Log2Know(name) |> Send2Cassandra[CountPerTile]("audi", "counts_per_tile")
      case _ =>
        println(s"${args.mkString(" ")} Not a valid option")
        System.exit(1)
      case "ignore_this" =>
        //Takes the enriched stream of vehicle events and calculates a set of filled tiles (adds timestamp for future queries)
        //Sends it back to kafka in another topic
        PullFromKafka[VehicleEvent](KafkaConfig(streamingContext = ssc,
          topic = "vehicles": String,
          groupId = "filled_tiles": String,
          bootstrapServers = Set("localhost:9092"): Set[String])
        ) |> Log2Know("filled_tiles") |> CalcFilledTiles(batchDuration) |> Send2Kafka[FilledTiles]("filled_tiles")
    }

    //sbt package && ~/tools/spark-2.2.0-bin-hadoop2.7/bin/spark-submit --class "com.auditest.Main" --packages com.typesafe.play:play-json_2.11:2.6.6,org.apache.spark:spark-streaming-kafka-0-10_2.11:2.2.0 --master local[4] target/scala-2.11/hopkins_2.11-0.1.0-SNAPSHOT.jar 1


    ssc.start()
    ssc.awaitTermination()
  }
}


