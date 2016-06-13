#!/bin/bash

DEFAULT_HEALTH_HOST=${DEFAULT_HEALTH_HOST:-localhost}

# build apps
./gradlew acceptanceTests --parallel
