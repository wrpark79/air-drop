#!/bin/sh

if [ ! -f "./VERSION" ]; then
    echo "ERROR: VERSION not found" >&2; exit 1;
fi

if [ ! -f "./config/application.properties" ]; then
    echo "ERROR: config/application.properties not found" >&2; exit 1;
fi

VERSION=`cat VERSION`
JAR_FILE="./airdrop-$VERSION.jar"

if [ ! -f "./airdrop-$VERSION.jar" ]; then
    echo "ERROR: airdrop-$VERSION.jar not found" >&2; exit 1;
fi

java -jar $JAR_FILE --spring.config.location=file:./config/application.properties &
