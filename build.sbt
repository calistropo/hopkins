import Dependencies._


organization := "com.example"
scalaVersion := "2.11.11"
version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(scalaTest % Test,
  //"com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
  "org.apache.spark" %% "spark-streaming" % "2.2.0",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.2.0",
  "com.typesafe.play" %% "play-json" % "2.6.6",
  "com.datastax.spark" %% "spark-cassandra-connector" % "2.0.3"
)

