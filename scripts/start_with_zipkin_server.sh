#!/bin/bash

set -e

RABBIT_PORT=${RABBIT_PORT:-9672}
DEFAULT_HEALTH_HOST=${DEFAULT_HEALTH_HOST:-localhost}
DEFAULT_ARGS="-DSPRING_RABBITMQ_HOST=\"${DEFAULT_HEALTH_HOST}\" -DSPRING_RABBITMQ_PORT=\"${RABBIT_PORT}\""

[[ -z "${MEM_ARGS}" ]] && MEM_ARGS="-Xmx128m -Xss1024k"

mkdir -p build

# build apps
./gradlew clean build --parallel

# run zipkin stuff
docker-compose kill
docker-compose build
docker-compose up -d

echo -e "\nStarting Zipkin Server..."
nohup $JAVA_HOME/bin/java $DEFAULT_ARGS $MEM_ARGS -jar zipkin-server/build/libs/*.jar > build/zipkin-server.log &

echo -e "\nStarting the apps..."
nohup $JAVA_HOME/bin/java $DEFAULT_ARGS $MEM_ARGS -jar service1/build/libs/*.jar > build/service1.log &
nohup $JAVA_HOME/bin/java $DEFAULT_ARGS $MEM_ARGS -jar service2/build/libs/*.jar > build/service2.log &
nohup $JAVA_HOME/bin/java $DEFAULT_ARGS $MEM_ARGS -jar service3/build/libs/*.jar > build/service3.log &
nohup $JAVA_HOME/bin/java $DEFAULT_ARGS $MEM_ARGS -jar service4/build/libs/*.jar > build/service4.log &
