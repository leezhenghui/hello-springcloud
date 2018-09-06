#!/bin/bash

export NOMAD_ADDR=
export NOMAD_CMD=/opt/nomad/bin/nomad

echo "Stopping zipkin-server-job"
$NOMAD_CMD job stop zipkin-server-job 

echo "Stopping addsvc-job"
$NOMAD_CMD job stop addsvc-job 

echo "Stopping subsvc-job"
$NOMAD_CMD job stop subsvc-job 

echo "Stopping calculator-ui-job"
$NOMAD_CMD job stop calculator-ui-job 

echo "Stopping api-gateway-job"
$NOMAD_CMD job stop api-gateway-job 

echo "Stopping hashi-ui"
$NOMAD_CMD job stop hashi-ui 

echo 'wait 15s...'
sleep 15
curl --request PUT  http://localhost:4646/v1/system/gc
echo 'force GC for nomad ...'
sleep 5 
echo 'done'
