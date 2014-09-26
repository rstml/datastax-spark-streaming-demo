This is a demo app which reads Tweets from Twitter Streaming API and stores hashtags in Cassandra grouped by day, hour and total count.

To setup Twitter credentials follow [this guide](http://ampcamp.berkeley.edu/big-data-mini-course/realtime-processing-with-spark-streaming.html) and then update [TwitterCredentials.scala](src/main/scala/com/datastax/examples/iskra/TwitterCredentials.scala#L8-L11) 

Cassandra
---------

In Cassandra add table:
```sql
CREATE KEYSPACE iskra WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE iskra.hashtags_by_interval (
    hashtag text,
    interval text,
    mentions counter,
    PRIMARY KEY(hashtag, interval)
) WITH CLUSTERING ORDER BY (interval DESC);
```

Stream Processing
-----------------

Start application locally:

```
sbt assembly run
```

To deploy Spark application on DSE cluster:

```
export SPARK_CLIENT_CLASSPATH=~/iskra/target/scala-2.10/iskra-assembly-1.0.jar
cd ~/iskra/
nohup dse spark-class com.datastax.examples.iskra.TwitterMonitor > stream.log &
```

Web Dashboard
-------------

Start web applicaiton:
```
cd web
./sbt
> container:start
```

Point your browser to http://localhost:8080/dashboard and watch counters updated in real time!

Other endpoints:

 * http://localhost:8080/total for total stats
 * http://localhost:8080/daily for daily stats
 
To deploy app to servlet container create war file using command below and deploy to the server:
```
cd web
./sbt
> package
```

Iskra?
------

Iskra means Spark in Russian and it was the name of [IBM XT Soviet clone](http://en.wikipedia.org/wiki/Iskra-1030).