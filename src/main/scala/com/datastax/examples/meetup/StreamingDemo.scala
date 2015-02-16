package com.datastax.examples.meetup

import com.datastax.spark.connector.cql.CassandraConnector
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Other configurable options - see [[StreamingSettings]], and /resources/application.conf
 * -Dspark.master, default is localhost
 * -Dspark.cassandra.connection.host, default is 127.0.0.1
 * -Dspark.cores.max, default configured is 2
 *
 * Verify data persisted after running in cqlsh with:
 * cqlsh> SELECT * FROM demo.events_by_interval;
 * 
 * You should output sequences similar to:
 * {{{
 *  event     | interval        | dimension         | subtotal
 * -----------+-----------------+-------------------+---------
 *  attending |             ALL |                ae |       1
 *  attending |             ALL |                au |      51
 *  attending |             ALL |                ca |      52
 *  attending |             ALL |                de |       2
 *  attending |             ALL |                es |       6
 *  attending |             ALL |                gb |      37
 *  attending |             ALL |                us |     941
 *  ...
 *   trending | S20141230011335 |  Self-Improvement |       8
 *   trending | S20141230011335 |           Singles |      14
 *   trending | S20141230011335 |            Social |      31
 *   trending | S20141230011335 | Social Networking |      22
 *  ...
 * }}}
 */
object StreamingDemo {

  val settings = new StreamingSettings
  import settings._

  val conf = new SparkConf(true)
    .setMaster(SparkMaster)
    .setAppName(getClass.getSimpleName)
    .setJars(DeployJars)
    .set("spark.executor.memory", SparkExecutorMemory.toString)
    .set("spark.cores.max", SparkCoresMax.toString)
    .set("spark.cassandra.connection.host", CassandraSeedNodes)

  createSchema()

  val sc = new SparkContext(conf)
  val ssc = new StreamingContext(sc, Seconds(StreamingBatchInterval))

  def main(args: Array[String]): Unit = {
    //ssc.checkpoint("meetup.dat")
    val stream = new PersistStreamByInterval
    stream.start(ssc, MeetupRSVPWebSocketUrl, CassandraKeyspace, CassandraTable)
  }

  /** Creates the keyspace and table schema. */
  def createSchema(): Unit = {
    CassandraConnector(conf).withSessionDo { session =>
      session.execute(s"DROP KEYSPACE IF EXISTS $CassandraKeyspace")
      session.execute(s"CREATE KEYSPACE IF NOT EXISTS $CassandraKeyspace WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1 }")
      session.execute(s"""
             CREATE TABLE IF NOT EXISTS $CassandraKeyspace.$CassandraTable (
                event text,
                interval text,
                dimension text,
                subtotal counter,
                PRIMARY KEY((event, interval), dimension)
            ) WITH CLUSTERING ORDER BY (dimension ASC)
           """)
    }
  }
}
