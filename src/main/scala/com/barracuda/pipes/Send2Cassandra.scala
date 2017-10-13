package com.barracuda.pipes

import com.datastax.spark.connector.cql.CassandraConnector
import com.datastax.spark.connector.mapper.ColumnMapper
import com.datastax.spark.connector.streaming._
import org.apache.spark.streaming.dstream.DStream

import scala.reflect.runtime.universe._

object Send2Cassandra {

  def apply[T: ColumnMapper : TypeTag](keySpace: String, table: String)(stream: DStream[T])(implicit connector:CassandraConnector) = {


    new DStreamFunctions(stream).saveToCassandra(
      keyspaceName = keySpace,
      tableName = table
    )
  }

}
