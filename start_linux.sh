#!/bin/bash

# build apps
./gradlew clean build --parallel

nohup $JAVA_HOME/bin/java -jar service1/build/libs/*.jar > build/service1.log &
nohup $JAVA_HOME/bin/java -jar service2/build/libs/*.jar > build/service2.log &
nohup $JAVA_HOME/bin/java -jar service3/build/libs/*.jar > build/service3.log &
nohup $JAVA_HOME/bin/java -jar service4/build/libs/*.jar > build/service4.log &
