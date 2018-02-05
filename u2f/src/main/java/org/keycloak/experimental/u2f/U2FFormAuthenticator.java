/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.experimental.u2f;

import com.yubico.u2f.U2F;
import com.yubico.u2f.data.DeviceRegistration;
import com.yubico.u2f.data.messages.SignRequestData;
import com.yubico.u2f.data.messages.SignResponse;
import com.yubico.u2f.exceptions.U2fBadInputException;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class U2FFormAuthenticator extends AbstractUsernameFormAuthenticator implements Authenticator {
    private final U2F u2f = new U2F();
    public static final String APP_ID = "https://localhost:8443";

    @Override
    public void action(AuthenticationFlowContext context) {
        try {
            MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
            String username = formData.getFirst("username");
            String response = formData.getFirst("tokenResponse");

            SignResponse signResponse = SignResponse.fromJson(response);

            SignRequestData signRequestData = SignRequestData.fromJson(context.getAuthenticationSession().getAuthNote("u2f-sign-data"));

            DeviceRegistration registration = u2f.finishSignature(signRequestData, signResponse, getRegistrations(username));

            context.success();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        Response challengeResponse = challenge(context, null);
        context.challenge(challengeResponse);
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    protected Response challenge(AuthenticationFlowContext context, String error) {
        try {
            SignRequestData signRequestData = u2f.startSignature(APP_ID, getRegistrations(context.getUser().getUsername()));

            context.getAuthenticationSession().setAuthNote("u2f-sign-data", signRequestData.toJson());

            LoginFormsProvider forms = context.form().setAttribute("request", signRequestData).setAttribute("username", context.getUser().getUsername());
            if (error != null) forms.setError(error);

            return forms.createForm("fido-u2f-login.ftl");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private Iterable<DeviceRegistration> getRegistrations(String username) throws U2fBadInputException {
        List<DeviceRegistration> registrations = new ArrayList<DeviceRegistration>();
        for (String serialized : TmpCredStore.creds.get(username).values()) {
            registrations.add(DeviceRegistration.fromJson(serialized));
        }
        return registrations;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return TmpCredStore.creds.containsKey(user.getUsername());
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
//        if (!user.getRequiredActions().contains(UserModel.RequiredAction.CONFIGURE_TOTP.name())) {
//            user.addRequiredAction(UserModel.RequiredAction.CONFIGURE_TOTP.name());
//        }

    }

    @Override
    public void close() {

    }
}
