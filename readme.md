This demo processes Meetup.com RSVP stream and stores results in Cassandra. The main purpose of this application is to demonstrate Spark Streaming capabilities in [Spark Cassandra Connector](https://github.com/datastax/spark-cassandra-connector) and DSE.

It produces two types of results:
 1. Total number of attendees per country (since application start). Refreshed every 5 seconds. Shows simple stream processing functionality.
 2. Trending meetup topics within the last 5 minutes. Refreshed every 10 seconds. Shows windowed transformations functionality.

Web dashboard is included for visualisation of these results:

![Screenshot](/screenshot.png?raw=true)

## Stream Processing

To start stream processing locally:

```
git clone git@github.com:rstml/datastax-spark-streaming-demo.git
cd datastax-spark-streaming-demo
sbt assembly run
```

To deploy Spark application on DSE cluster:

```
dse spark-submit --class com.datastax.examples.meetup.StreamingDemo ./target/scala-2.10/streaming-demo.jar -Dspark.cassandra.connection.host=127.0.0.1
```

Input options:
* -Dspark.master - Autodetect in DSE. Specify for non-DSE deployments.
* -Dspark.cassandra.connection.host - Default is 127.0.0.1, replace with rpc_address of one of the nodes.
* -Dspark.cores.max - Default configured is 2
* see resources/applicaiton.conf for more

## Web Dashboard

To start web applicaiton:
```
cd web
./sbt
> container:start
```

Point your browser to [localhost:8080](http://localhost:8080/) and watch map and topics update in real time.

Other endpoints:

 * [/countries](http://localhost:8080/countries) - attendees by country since start
 * [/trending](http://localhost:8080/trending)  - trending topics within last 5 minutes
 
To deploy app to a servlet container, create war package using command below:
```
cd web
./sbt
> package
```

## License

This software is available under the [Apache License, Version 2.0](LICENSE.txt).