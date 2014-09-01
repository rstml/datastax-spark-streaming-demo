This is sample app which reads Tweets from Twitter firehose and stores top hash tags in Cassandra grouped by hour.

Iskra? Iskra means Spark in Russian.

To setup Twitter credentials follow http://ampcamp.berkeley.edu/big-data-mini-course/realtime-processing-with-spark-streaming.html and then update `TwitterCredentials.scala` 

In Cassandra add table:
```sql
CREATE KEYSPACE iskra WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE hashtags_by_hour (
    hour_start timestamp,
    hashtag text,
    count counter,
    PRIMARY KEY(hour_start, hashtag)
);
```

To start application:

```
sbt assembly run
```