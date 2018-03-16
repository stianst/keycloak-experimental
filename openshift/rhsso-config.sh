#!/bin/bash

CONFIG=$PWD/.keycloak/kcadmin.config
SERVER=$1
PASS=$2

# TODO Use HTTPS and setup truststore

./kcadm.sh config credentials --config $CONFIG --server $SERVER --realm master --user admin --password $PASS

./kcadm.sh create realms --config $CONFIG -f realm-summit.json
