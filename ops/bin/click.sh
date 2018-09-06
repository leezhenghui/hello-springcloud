#!/bin/bash

curl -X POST "http://10.10.10.200:2809/calculator-ui/api/v1/compute" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"expression\": \"1+1+2+3+4+5+6-1-2-3-4-5-6+9+8-4-5\"}"
echo ""
