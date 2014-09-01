package com.datastax.examples.iskra

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.SparkContext._
import org.apache.spark.streaming.twitter._
import com.datastax.spark.connector.streaming._
import com.github.nscala_time.time.Imports._

object TwitterMonitor
{
  def main(args: Array[String]): Unit =
  {
    // Setup Twitter access
    TwitterCredentials.setCredentials()

    val conf = new SparkConf()
      .setMaster("spark://127.0.0.1:7077")
      .setAppName("TwitterMonitor")
      .setJars(Array("target/scala-2.10/iskra-assembly-1.0.jar"))
      //.setJars(StreamingContext.jarOfClass(this.getClass))
      .set("spark.cassandra.connection.host", "localhost")
      .set("spark.cleaner.ttl", "3600")

    val ssc = new StreamingContext(conf, Seconds(3))

    val stream = TwitterUtils.createStream(ssc, None)

    val hashTags = stream.flatMap(status => status.getText.split(" ").filter(_.startsWith("#")))

    val topCounts = hashTags.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(3))
      .map{case (topic, count) => (count, topic)}
      .transform(_.sortByKey(false))
      .map{case (count, topic) => (DateTime.now.minute(0).second(0).millis(0), topic, count)}

    //topCounts.print()

    topCounts.saveToCassandra("iskra", "hashtags_by_hour", Seq("hour_start", "hashtag", "count"))

    ssc.start()
    ssc.awaitTermination()
  }
}