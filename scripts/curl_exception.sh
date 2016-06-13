#!/bin/bash

set -e

echo -e "Sending a request to service1"

curl --fail localhost:8081/readtimeout
