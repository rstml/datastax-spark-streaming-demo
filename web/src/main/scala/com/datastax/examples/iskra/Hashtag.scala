package com.datastax.examples.iskra

import com.datastax.driver.core.{Cluster, Session, Row}
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.Implicits._
import scala.concurrent.Future

/**
 * Created by rustam on 22/09/2014.
 */
case class HashtagModel (
                          hashtag: String,
                          interval: String,
                          mentions: Long
                          )

sealed class HashtagRecord extends CassandraTable[HashtagRecord, HashtagModel]
{
  override val tableName = "hashtags_by_interval"
  object hashtag extends StringColumn(this) with PartitionKey[String]
  object interval extends StringColumn(this) with ClusteringOrder[String] with Descending
  object mentions extends CounterColumn(this)

  override def fromRow(row: Row): HashtagModel = {
    HashtagModel(
      hashtag(row),
      interval(row),
      mentions(row)
    )
  }
}

object Hashtag extends HashtagRecord
{
  val cluster = Cluster.builder().addContactPoint("127.0.0.1").build()
  implicit val session = cluster.connect("iskra")

  def hourly(hashtag: String): Future[Seq[(String, Long)]] = {
    select (_.interval, _.mentions) where (_.hashtag eqs hashtag) and (_.interval gte "M") and (_.interval lt "N") limit 60 fetch
  }

  def totals(hashtags: List[String]): Future[Seq[(String, Long)]] = {
    select (_.hashtag, _.mentions) where (_.hashtag in hashtags) and (_.interval eqs "ALL") limit 60 fetch
  }

}