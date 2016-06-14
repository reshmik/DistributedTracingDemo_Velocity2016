#!/bin/bash

set -e

echo -e "Sending a request to service1"

curl --fail localhost:8081/start && echo -e "\nIt worked!" || echo -e "\nFailed to send the request" && exit 1
