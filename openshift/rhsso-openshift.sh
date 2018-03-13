#!/bin/bash -e

PROJECT=rh-sso
export PW=password


## Generate certs ##

rm -f keystore.jks truststore.jks jgroups.jceks sso.crt

keytool -genkey -alias localhost -keyalg RSA -keypass password -storepass password -keystore keystore.jks  -dname 'cn=localhost, ou=localhost, o=localhost, c=NO' -validity 30
keytool -export -alias localhost -storepass password -file sso.crt -keystore keystore.jks
keytool -import -v -trustcacerts -alias localhost -file sso.crt -keystore truststore.jks -keypass password -storepass password -noprompt

keytool -genseckey -alias jgroups -storetype JCEKS -keystore jgroups.jceks -keypass $PW -storepass $PW


## Create app ##

oc new-project $PROJECT
oc create serviceaccount sso-service-account
oc policy add-role-to-user view system:serviceaccount:sso-app-demo:sso-service-account

oc secret new sso-jgroup-secret jgroups.jceks
oc secret new sso-ssl-secret keystore.jks truststore.jks

oc secrets link sso-service-account sso-jgroup-secret sso-ssl-secret

oc new-app --template=sso71-postgresql \
-p APPLICATION_NAME=rhsso \
-p HTTPS_SECRET=sso-ssl-secret \
-p HTTPS_PASSWORD=$PW \
-p SSO_ADMIN_USERNAME=admin \
-p SSO_ADMIN_PASSWORD=admin \
-p SSO_TRUSTSTORE_SECRET=sso-ssl-secret \
-p SSO_TRUSTSTORE_PASSWORD=$PW \
-p JGROUPS_ENCRYPT_SECRET=sso-jgroup-secret \
-p JGROUPS_ENCRYPT_PASSWORD=$PW

# Work around for https://issues.jboss.org/browse/CLOUD-2195
oc volume dc/rhsso --add --claim-size 32M --mount-path /opt/eap/standalone/configuration/standalone_xml_history --name standalone-xml-history
