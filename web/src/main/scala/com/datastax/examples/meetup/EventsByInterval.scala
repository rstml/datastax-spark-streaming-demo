package com.datastax.examples.meetup

import com.datastax.driver.core.{Cluster, Session, Row}
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.Implicits._
import scala.concurrent.Future

/**
 * Created by rustam on 22/09/2014.
 */
case class EventModel (
                event: String,
                interval: String,
                dimension: String,
                subtotal: Long
            )

sealed class EventRecord extends CassandraTable[EventRecord, EventModel]
{
  override val tableName = "events_by_interval"
  object event extends StringColumn(this) with PartitionKey[String]
  object interval extends StringColumn(this) with ClusteringOrder[String] with Descending
  object dimension extends StringColumn(this) with ClusteringOrder[String] with Ascending
  object subtotal extends CounterColumn(this)

  override def fromRow(row: Row): EventModel = {
    EventModel(
      event(row),
      interval(row),
      dimension(row),
      subtotal(row)
    )
  }
}

object Event extends EventRecord
{
  val keyspace = "demo"
  val cluster = Cluster.builder().addContactPoint("127.0.0.1").build()
  implicit val session = cluster.connect(keyspace)

//  def hourly(hashtag: String): Future[Seq[(String, Long)]] = {
//    select (_.interval, _.subtotal) where (_.event eqs hashtag) and (_.interval gte "M") and (_.interval lt "N") limit 60 fetch
//  }

  def dimensions(event: String, interval: String): Future[Seq[(String, Long)]] = {
    select (_.dimension, _.subtotal) where (_.event eqs event) and (_.interval eqs interval) limit 500 fetch
  }

}