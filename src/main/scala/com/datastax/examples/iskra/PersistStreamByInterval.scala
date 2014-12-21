package com.datastax.examples.iskra

import com.datastax.examples.iskra.model.MeetupRsvp
import com.datastax.examples.iskra.websocket._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}
import org.joda.time.{DateTime, DateTimeZone}

import scala.util.matching.Regex

class PersistStreamByInterval extends Serializable {

  def start(ssc: StreamingContext, filters: Regex, keyspace: String, table: String): Unit = {

    val stream = ssc.receiverStream[MeetupRsvp](new WebSocketReceiver("ws://stream.meetup.com/2/rsvps", StorageLevel.MEMORY_ONLY_SER))
//    stream.checkpoint(Seconds(60))
//    stream.repartition(2)
    val rsvp = stream.map(rsvp => rsvp.group.group_country)
    rsvp.print()

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


