#!/usr/bin/env bash

TOP_DIR="."
SOURCE_PATH="$TOP_DIR/src"
DEST_PATH="$TOP_DIR/bin"

for jar in $TOP_DIR/lib/*.jar; do
	CLASSPATH="${CLASSPATH}:$jar"
done

export CLASSPATH="$CLASSPATH:$DEST_PATH"

mkdir -p $DEST_PATH

javac -d $DEST_PATH $SOURCE_PATH/*.java

if [ $? -eq 0 ]; then
	java org.junit.runner.JUnitCore JsonPullParserTest
fi

