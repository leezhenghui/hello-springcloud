#!/bin/bash

cd /opt/kafka_2.12-1.0.2/bin

./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic calculator-ui-exectue-counter 
