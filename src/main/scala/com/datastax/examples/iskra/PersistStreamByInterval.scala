package com.datastax.examples.iskra

import com.datastax.examples.iskra.model.MeetupRsvp
import com.datastax.examples.iskra.model.EventInterval
import com.datastax.examples.iskra.websocket._
import com.datastax.spark.connector._
import com.datastax.spark.connector.streaming._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, Minutes, StreamingContext}
import org.apache.spark.streaming.StreamingContext._

class PersistStreamByInterval extends Serializable {

  val tableColumns = SomeColumns("event", "interval", "dimension", "subtotal")

  def start(ssc: StreamingContext, websocket: String, keyspace: String, table: String) {

    val stream = ssc.receiverStream[MeetupRsvp](new WebSocketReceiver(websocket, StorageLevel.MEMORY_ONLY_SER))
    //stream.checkpoint(Seconds(60))
    //stream.repartition(2)

    // Filter Accepted RSVP
    val rsvpAccepted = stream.filter(_.response == "yes")

    // Number of attendees by Country
    val rsvpByCountry = rsvpAccepted
      .map( rsvp => (rsvp.group.group_country, rsvp.guests + 1) )
      .reduceByKey(_ + _)
      .map{ case (country, attendees) => ("attending", EventInterval.All, country, attendees) }

    rsvpByCountry.saveToCassandra(keyspace, table, tableColumns)

    // Trending Topics
    val trendingTopics = rsvpAccepted
      .flatMap( rsvp => rsvp.group.group_topics )
      .map( topic => (topic.topic_name, 1) )
      .reduceByKeyAndWindow((a:Int,b:Int) => a+b, Minutes(5), Seconds(10))
      .filter( t => t._2 > 5 ) // min threshold = 5
      .transform( (rdd, time) => rdd.map { case (topic, count) => ("trending", EventInterval.Seconds(time), topic, count)} )

    trendingTopics.saveToCassandra(keyspace, table, tableColumns)

    ssc.start()
    ssc.awaitTermination()
  }
}


