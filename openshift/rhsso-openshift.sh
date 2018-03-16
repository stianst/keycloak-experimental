#!/bin/bash -e

PROJECT=sso
#export PW=`openssl rand -base64 32`
export PW=password

export ADMIN_PW=`openssl rand -base64 32`

## Generate certs ##

rm -f keystore.jks truststore.jks jgroups.jceks sso.crt

keytool -genkey -alias localhost -keyalg RSA -keypass password -storepass password -keystore keystore.jks  -dname 'cn=localhost, ou=localhost, o=localhost, c=NO' -validity 300
keytool -export -alias localhost -storepass password -file sso.crt -keystore keystore.jks
keytool -import -v -trustcacerts -alias localhost -file sso.crt -keystore truststore.jks -keypass password -storepass password -noprompt

keytool -genseckey -alias jgroups -storetype JCEKS -keystore jgroups.jceks -keypass $PW -storepass $PW

## Create project ##

oc new-project $PROJECT

## Import images ##

oc replace -n $PROJECT --force -f https://raw.githubusercontent.com/jboss-openshift/application-templates/ose-v1.4.9/sso/sso72-image-stream.json

sleep 20

oc -n $PROJECT import-image redhat-sso72-openshift:1.0

## Service account and secrets ##

#oc create serviceaccount sso-service-account
#oc policy add-role-to-user view system:serviceaccount:sso-app-demo:sso-service-account

oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default

oc secret new sso-jgroup-secret jgroups.jceks
oc secret new sso-ssl-secret keystore.jks truststore.jks

#oc secrets link sso-service-account sso-jgroup-secret sso-ssl-secret
oc secrets link default sso-jgroup-secret sso-ssl-secret

## Create app ##

oc new-app -f sso72-mysql-persistent.json \
-p IMAGE_STREAM_NAMESPACE=sso \
-p APPLICATION_NAME=sso \
-p HTTPS_SECRET=sso-ssl-secret \
-p HTTPS_PASSWORD=$PW \
-p SSO_ADMIN_USERNAME=admin \
-p SSO_ADMIN_PASSWORD=$ADMIN_PW \
-p SSO_TRUSTSTORE_SECRET=sso-ssl-secret \
-p SSO_TRUSTSTORE_PASSWORD=$PW \
-p JGROUPS_ENCRYPT_SECRET=sso-jgroup-secret \
-p JGROUPS_ENCRYPT_PASSWORD=$PW \
-p MEMORY_LIMIT=2Gi

echo "Admin password $ADMIN_PW"

# Work around for https://issues.jboss.org/browse/CLOUD-2195
#oc volume dc/rhsso --add --claim-size 32M --mount-path /opt/eap/standalone/configuration/standalone_xml_history --name standalone-xml-history
