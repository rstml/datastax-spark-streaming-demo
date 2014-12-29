package com.datastax.examples.iskra

import scala.sys.process._
import akka.japi.Util.immutableSeq
import com.typesafe.config.ConfigFactory

final class StreamingSettings {

  protected val config = ConfigFactory.load.getConfig("streaming-app")

  val MeetupRSVPWebSocketUrl = config.getString("meetup.ws.url")

  /** Attempts to detect System property, falls back to config. */
  // Detect Spark Master and Cassandra entry point using dsetool
  val SparkMaster: String = try { "dsetool sparkmaster".!!.trim }
      catch { case x:Exception => sys.props.get("spark.master").getOrElse(config.getString("spark.master")) }

  val StreamingBatchInterval = config.getInt("spark.streaming.batch.interval")

  val SparkExecutorMemory = config.getBytes("spark.executor.memory")

  val SparkCoresMax = sys.props.get("spark.cores.max").getOrElse(config.getInt("spark.cores.max"))

  val DeployJars: Seq[String] = immutableSeq(
    config.getStringList("spark.jars")).filter(new java.io.File(_).exists)

  /** Attempts to detect System property, falls back to config,
    * to produce a comma-separated string of hosts. */
  val CassandraSeedNodes: String = sys.props.get("spark.cassandra.connection.host") getOrElse
        immutableSeq(config.getStringList("spark.cassandra.connection.host")).mkString(",")

  val CassandraKeyspace: String = config.getString("spark.cassandra.keyspace")

  val CassandraTable: String = config.getString("spark.cassandra.table")

}