This is a demo app which processes Meetup.com RSVPs and stores in Cassandra:
 1. Number of attendees per country
 2. Trending Meetup Topics

This app also provides web dashboard for visualisation of the results.

Stream Processing
-----------------

To start stream processing locally:

```
git clone git@github.com:rstml/iskra.git
cd iskra
sbt assembly run
```

To deploy Spark application on DSE 4.6 cluster:

```
dse spark-submit --class com.datastax.examples.iskra.StreamingApp ./target/scala-2.10/iskra.jar -Dspark.cassandra.connection.host=127.0.0.1 
```

Web Dashboard
-------------

To start web applicaiton:
```
cd web
./sbt
> container:start
```

Point your browser to http://localhost:8080/ and watch map and topics update in real time.

Other endpoints:

 * http://localhost:8080/countries - attendees by country since start
 * http://localhost:8080/trending  - trending topics within last 5 minutes
 
To deploy app to a servlet container, create war package using command below:
```
cd web
./sbt
> package
```

Iskra?
------

Iskra means Spark in Russian and it was the name of [IBM XT Soviet clone](http://en.wikipedia.org/wiki/Iskra-1030).
