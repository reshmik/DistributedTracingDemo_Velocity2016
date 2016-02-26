#!/bin/bash

# build apps
./gradlew clean build --parallel

# run zipkin stuff
docker-compose up -d

ZIPKIN_HOST="-Dspring.zipkin.baseUrl=http://${DEFAULT_HEALTH_HOST}:9411"

nohup $JAVA_HOME/bin/java ${ZIPKIN_HOST} -jar service1/build/libs/*.jar > build/service1.log &
nohup $JAVA_HOME/bin/java ${ZIPKIN_HOST} -jar service2/build/libs/*.jar > build/service2.log &
nohup $JAVA_HOME/bin/java ${ZIPKIN_HOST} -jar service3/build/libs/*.jar > build/service3.log &
nohup $JAVA_HOME/bin/java ${ZIPKIN_HOST} -jar service4/build/libs/*.jar > build/service4.log &