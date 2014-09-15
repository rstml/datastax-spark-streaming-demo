This is a demo app which reads Tweets from Twitter Streaming API and stores hashtags in Cassandra grouped by day, hour and total count.

To setup Twitter credentials follow [this guide](http://ampcamp.berkeley.edu/big-data-mini-course/realtime-processing-with-spark-streaming.html) and then update [TwitterCredentials.scala](src/main/scala/com/datastax/examples/iskra/TwitterCredentials.scala#L8-L11) 

In Cassandra add table:
```sql
CREATE KEYSPACE iskra WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE iskra.hashtags_by_interval (
    hashtag text,
    interval text,
    count counter,
    PRIMARY KEY(hashtag, interval)
);
```

To start application locally:

```
sbt assembly run
```

To start application on DSE cluster:

```
export SPARK_CLIENT_CLASSPATH=~/iskra/target/scala-2.10/iskra-assembly-1.0.jar
cd ~/iskra/
nohup dse spark-class com.datastax.examples.iskra.TwitterMonitor > stream.log &
```

_Iskra?_ Iskra means Spark in Russian and it was the name of [IBM XT Soviet clone](http://en.wikipedia.org/wiki/Iskra-1030).