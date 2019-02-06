# FIDO U2F Authenticator for Keycloak

## Notes

* Requires HTTPS. U2F will fail otherwise

## TODO

* Allow user to manage registrations through account management console
* Attestation - allow specifying what devices should be supported 
* Metadata - allow users to view metadata about registered devices
* Allow admin to view details about registered devices

## Usage

1. Deploy to Keycloak:

    mvn clean install wildfly:deploy

2. Login to admin console and create authentication flow with U2F

   * Go to Authentication
   * Under Flows select Browser and click Copy
   * Remove OTP Form under Copy Of Browser Forms and add U2F in same place
   * Mark U2F as optional
   * Click Bindings and switch Browser Flow to Copy of browser
   * Click Required Actions and Register
   * Select Register U2F and click Ok

3. Add `Configure U2F` required action to admin user

   * Go to Users
   * View all users
   * Click admin
   * In Required User Actions add Register U2F

4. Logout

5. Login as admin and configure U2F when requested

6. Logout

7. Login again and you should now be requested to touch the U2F token to continue
