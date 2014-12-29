package com.datastax.examples.iskra

import com.datastax.examples.iskra.model.MeetupRsvp
import com.datastax.examples.iskra.websocket._
import com.datastax.spark.connector._
import com.datastax.spark.connector.streaming._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}
import org.apache.spark.streaming.StreamingContext._
import org.joda.time.{DateTime, DateTimeZone}

class PersistStreamByInterval extends Serializable {

  def start(ssc: StreamingContext, websocket: String, keyspace: String, table: String) {

    val stream = ssc.receiverStream[MeetupRsvp](new WebSocketReceiver(websocket, StorageLevel.MEMORY_ONLY_SER))
//    stream.checkpoint(Seconds(60))
//    stream.repartition(2)

    val rsvpByCountry = stream
      .filter(_.response == "yes")
      .map( rsvp => (rsvp.group.group_country, rsvp.guests + 1) )
      .reduceByKey(_ + _)
      .map { case (country, attendees) => ("attending", "ALL", country, attendees) }

    //rsvpByCountry.print()
    rsvpByCountry.saveToCassandra(keyspace, table, SomeColumns("event", "interval", "dimension", "subtotal"))

//    val transform = (cruft: String) => filters.findAllIn(cruft).flatMap(_.stripPrefix("#"))
//
//    /** Note that Cassandra is doing the sorting for you here. */
//    stream.flatMap(_.getText.toLowerCase.split("""\s+"""))
//      .map(transform)
//      .countByValue()
//      //.countByValueAndWindow(Seconds(5), Seconds(5))
//      .transform((rdd, time) => rdd.map { case (term, count) => (term, count, now(time))})
//      .saveToCassandra(keyspace, table, SomeColumns("hashtag", "mentions", "interval"))

    ssc.start()
    ssc.awaitTermination()
  }

  private def now(time: Time): String =
    new DateTime(time.milliseconds, DateTimeZone.UTC).toString("yyyyMMddHH:mm:ss.SSS")
}


