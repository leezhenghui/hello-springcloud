#!/bin/bash

cd /opt/wrk
./wrk -t 5 -c 5 -s ./scripts/calculator.lua -d 60s --latency http://localhost:2809/calculator-ui/api/v1/compute
