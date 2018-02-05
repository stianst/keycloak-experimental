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
import com.yubico.u2f.data.messages.RegisterRequestData;
import com.yubico.u2f.data.messages.RegisterResponse;
import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.forms.login.freemarker.model.UrlBean;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.theme.Theme;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class UpdateU2F implements RequiredActionProvider, RequiredActionFactory {

    public static final String APP_ID = "https://localhost:8443";

    private final U2F u2f = new U2F();

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        try {
            RegisterRequestData registerRequestData = u2f.startRegistration(APP_ID, new LinkedList<>());

            context.getAuthenticationSession().setAuthNote("u2f-registration-data", registerRequestData.toJson());

            Response challenge = context.form()
                    .setAttribute("url", new UrlBean(context.getRealm(), context.getSession().theme().getTheme(Theme.Type.LOGIN), context.getSession().getContext().getUri().getBaseUri(), context.getActionUrl()))
                    .setAttribute("request", registerRequestData)
                    .setAttribute("username", context.getUser().getUsername())
                    .createForm("fido-u2f-register.ftl");

            context.challenge(challenge);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processAction(RequiredActionContext context) {
        try {
            MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
            String username = formData.getFirst("username");
            String tokenResponse = formData.getFirst("tokenResponse");

            RegisterResponse registerResponse = RegisterResponse.fromJson(tokenResponse);

            RegisterRequestData registerRequestData = RegisterRequestData.fromJson(context.getAuthenticationSession().getAuthNote("u2f-registration-data"));

            DeviceRegistration registration = u2f.finishRegistration(registerRequestData, registerResponse);
            addRegistration(username, registration);

            context.success();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void addRegistration(String username, DeviceRegistration registration) {
        if (!TmpCredStore.creds.containsKey(username)) {
            TmpCredStore.creds.put(username, new HashMap<>());
        }
        TmpCredStore.creds.get(username).put(registration.getKeyHandle(), registration.toJson());
    }

    @Override
    public void close() {

    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public String getDisplayText() {
        return "Configure U2F";
    }


    @Override
    public String getId() {
        return "REGISTER_U2F";
    }

    @Override
    public boolean isOneTimeAction() {
        return true;
    }
}
