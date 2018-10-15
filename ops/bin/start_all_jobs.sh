#!/bin/bash

export NOMAD_ADDR=
export NOMAD_CMD=/opt/nomad/bin/nomad

echo "Starting hashi-ui"
$NOMAD_CMD run /vagrant/deployable/hashi-ui.hcl
echo "Starting add service"
$NOMAD_CMD run /vagrant/deployable/addsvc.dev.hcl
echo "Starting subtract service"
$NOMAD_CMD run /vagrant/deployable/subsvc.dev.hcl
echo "Starting calculator-ui service"
$NOMAD_CMD run /vagrant/deployable/calculator-ui.dev.hcl
echo "Starting api-gateway service"
$NOMAD_CMD run /vagrant/deployable/api-gateway.dev.hcl
echo "Starting api-gateway service"
$NOMAD_CMD run /vagrant/deployable/springboot-admin.dev.hcl
echo "Starting zipin-server service"
$NOMAD_CMD run /vagrant/deployable/zipkin-server.dev.hcl
