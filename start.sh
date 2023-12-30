#!/bin/bash

cd /opt/kafka/kafka_2.13-3.6.1/

./bin/zookeeper-server-start.sh config/zookeeper.properties &
process_id=$!
./bin/kafka-server-start.sh config/server.properties

wait $process_id

exit $?