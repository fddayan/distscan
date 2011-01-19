#!/bin/sh

HBASE_JAR=/opt/hbase/hbase-0.20.4.jar
HADOOP_JAR=/opt/hadoop/hadoop-0.20.2-core.jar
ZOOKEEPER_JAR=/opt/hbase/lib/zookeeper-3.2.2.jar
ADDITIONAL_CLASSPATH=/opt/hbase/lib/commons-logging-1.0.4.jar:/opt/hbase/lib/commons-logging-api-1.0.4.jar:/opt/hbase/lib/log4j-1.2.15.jar:

java -cp $ADDITIONAL_CLASSPATH:$HBASE_JAR:$HADOOP_JAR:$ZOOKEEPER_JAR:distscan-1.0-SNAPSHOT-jar-with-dependencies.jar com.brilig.Cli $*
