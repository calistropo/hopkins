#!/bin/sh

export JOBS_DEPS=com.typesafe.play:play-json_2.11:2.6.6,org.apache.spark:spark-streaming-kafka-0-10_2.11:2.2.0,datastax:spark-cassandra-connector:2.0.1-s_2.11
for var in "$@"
do
  /spark/bin/spark-submit --master local[4] --packages $JOBS_DEPS --class "com.calistropo.Main" jobs.jar $var
done

