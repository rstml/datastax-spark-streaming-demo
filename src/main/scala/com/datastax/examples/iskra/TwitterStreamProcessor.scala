package com.datastax.examples.iskra

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.twitter._
import com.datastax.spark.connector.streaming._
import com.github.nscala_time.time.Imports._

/**
 * Created by rustam on 13/09/2014.
 */
object TwitterStreamProcessor
{
  val keyspace = "iskra"
  val table = "hashtags_by_interval"
  val columns = Seq("hashtag", "mentions", "interval")
  val tags = Seq("iphone", "android")

  def start(master: String = "spark://127.0.0.1:7077", cassandraIp: String = "127.0.0.1")
  {
    val sc = new SparkConf()
      .setMaster(master)
      .setAppName("Twitter-Demo")
      .setJars(Array(System.getProperty("user.dir") + "/target/scala-2.10/iskra-assembly-1.0.jar"))
      .set("spark.executor.memory", "1g")
      .set("spark.cores.max", "6")
      .set("spark.cassandra.connection.host", cassandraIp)
//      .set("spark.cleaner.ttl", "3600")

    val ssc = new StreamingContext(sc, Seconds(5))

    val stream = TwitterUtils.
      createStream(ssc, None, Nil, storageLevel = StorageLevel.MEMORY_ONLY_SER_2)
      //.repartition(3)

    val hashTags = stream.flatMap(tweet =>
      tweet.getText.toLowerCase.split(" ").transform(_.stripPrefix("#")).filter(tags.contains(_)))

    //val tagCounts = hashTags.map((_, 1)).reduceByKey(_ + _)

    val tagCountsByMinute = hashTags.map((_, 1)).reduceByKey(_ + _)
      .map{case (hashtag, mentions) => (hashtag, mentions, "M" + DateTime.now.toString("yyyyMMddHHmm"))}

    val tagCountsByHour = hashTags.map((_, 1)).reduceByKey(_ + _)
      .map{case (hashtag, mentions) => (hashtag, mentions, "H" + DateTime.now.toString("yyyyMMddHH"))}

    val tagCountsByDay  = hashTags.map((_, 1)).reduceByKey(_ + _)
      .map{case (hashtag, mentions) => (hashtag, mentions, "D" + DateTime.now.toString("yyyyMMdd"))}

    val tagCountsAll    = hashTags.map((_, 1)).reduceByKey(_ + _)
      .map{case (hashtag, mentions) => (hashtag, mentions, "ALL")}

    //tagCountsByHour.print()

    tagCountsByMinute.saveToCassandra(keyspace, table, columns)
    tagCountsByHour.saveToCassandra(keyspace, table, columns)
    tagCountsByDay.saveToCassandra(keyspace, table, columns)
    tagCountsAll.saveToCassandra(keyspace, table, columns)

    ssc.start()
    ssc.awaitTermination()
  }

}