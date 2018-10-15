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

echo "Stopping springboot-admin-job"
$NOMAD_CMD job stop springboot-admin-job

echo "Stopping hashi-ui"
$NOMAD_CMD job stop hashi-ui 

echo 'wait 5s...'
sleep 5
curl --request PUT  http://localhost:4646/v1/system/gc
echo 'force GC for nomad, await for work directory cleanup...'
sleep 15
echo 'done'
