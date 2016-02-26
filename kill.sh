#!/bin/bash

kill `jps | grep "1.0.0.jar" | cut -d " " -f 1`