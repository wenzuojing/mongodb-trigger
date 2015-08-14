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

JAVA_OPTS="-Xms256m -Xmx512m -XX:PermSize=256m -XX:MaxPermSize=512m"

echo "JAVA_HOME  :$JAVA_HOME"
echo "SERVER_HOME:$SERVER_HOME"
echo "CLASSPATH  :$CLASSPATH"

exec  $java -classpath  $CLASSPATH  $JAVA_OPTS org.wzj.mongodb.trigger.Bootstrap


