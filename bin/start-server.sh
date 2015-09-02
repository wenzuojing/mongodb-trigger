#! /bin/sh

if [ -z "$JAVA_HOME" ] ; then 
	export JAVA_HOME=/usr/local/java
fi

SCRIPT="$0"
while [ -h "$SCRIPT" ] ; do
  ls=`ls -ld "$SCRIPT"`
  # Drop everything prior to ->
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    SCRIPT="$link"
  else
    SCRIPT=`dirname "$SCRIPT"`/"$link"
  fi
done

SERVER_HOME=`dirname "$SCRIPT"`
SERVER_HOME=`cd "$SERVER_HOME"; pwd`
export SERVER_HOME

LIBDIR=$SERVER_HOME/lib

CLASSPATH=${CLASSPATH}:${SERVER_HOME}/conf

for lib in ${LIBDIR}/*.jar
do
 CLASSPATH=$CLASSPATH:$lib
done

java=$JAVA_HOME/bin/java

JAVA_OPTS="
-Xmx1G
-Xms1G
-XX:PermSize=128M 
-XX:MaxPermSize=256M  
-XX:+UseConcMarkSweepGC 
-XX:+UseParNewGC
-XX:+CMSConcurrentMTEnabled
-XX:+CMSParallelRemarkEnabled 
-XX:+UseCMSCompactAtFullCollection 
-XX:CMSFullGCsBeforeCompaction=0 
-XX:+CMSClassUnloadingEnabled 
-XX:LargePageSizeInBytes=128M 
-XX:+UseFastAccessorMethods 
-XX:+UseCMSInitiatingOccupancyOnly 
-XX:CMSInitiatingOccupancyFraction=80 
-XX:SoftRefLRUPolicyMSPerMB=0 
-XX:+PrintClassHistogram
-XX:+PrintGCDetails 
-XX:+PrintGCTimeStamps 
-XX:+PrintHeapAtGC 
-Xloggc:log/gc.log
"

echo "JAVA_HOME  :$JAVA_HOME"
echo "SERVER_HOME:$SERVER_HOME"
echo "CLASSPATH  :$CLASSPATH"

exec  $java -classpath  $CLASSPATH  $JAVA_OPTS org.wzj.mongodb.trigger.Bootstrap


