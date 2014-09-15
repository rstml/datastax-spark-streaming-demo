# DSE only
export SPARK_CLIENT_CLASSPATH=~/iskra/target/scala-2.10/iskra-assembly-1.0.jar
cd ~/iskra/
nohup dse spark-class com.datastax.examples.iskra.TwitterMonitor > stream.log &