#!/bin/bash

if [ -z "$1" ]; then
  echo "Usage: ./install_keep_jar_to_maven.sh <path-to-keep-installation>"
  exit 1
fi

KEEP_PATH=$1

#Find the latest keep-core jar
KEEP_JAR=$(find $KEEP_PATH -name 'keep-core-*.jar' | sort | tail -n 1)
KEEP_JAVADOC_JAR=$(find $KEEP_PATH -name 'keep-core-*-javadoc.jar' | sort | tail -n 1)
KEEP_VERSION=$(basename $KEEP_JAR | sed -n 's/keep-core-\(.*\).jar/\1/p')

mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file \
        -Dfile=$KEEP_JAR \
        -Djavadoc=$KEEP_JAVADOC_JAR

echo Keep $KEEP_VERSION installed.
