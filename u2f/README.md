TODO
----

* Use action token to store register data and sign data (or authentication session)
* Use credential provider to store credentials


Usage
-----

ln -s $PWD/src/main/templates/fido-u2f-register.ftl ~/kc/keycloak/themes/base/login/
ln -s $PWD/src/main/templates/fido-u2f-login.ftl ~/kc/keycloak/themes/base/login/
cp src/main/templates/u2f-api-1.1.js ~/kc/keycloak/themes/keycloak/login/resources/

mvn clean install wildfly:deploy

Login to admin console

Create authentication flow copy with U2F executor

Add Configure U2F action to admin user

Logout

Login as admin

Configure U2F

Logout

Login as admin

U2F time!!



Notes - Temporary in-mem storage of U2F data including user creds! Lost on KC restart
