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
import org.jboss.logging.Logger;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.common.util.UriUtils;
import org.keycloak.forms.login.freemarker.model.UrlBean;
import org.keycloak.services.Urls;
import org.keycloak.theme.Theme;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class U2FRequiredActionProvider implements RequiredActionProvider {

    private static final Logger logger = Logger.getLogger(U2FRequiredActionProvider.class);

    public static final String U2F_REGISTRATION_DATA = "u2f-registration-data";

    private final U2F u2f = new U2F();

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        logger.debugv("Sending registration, session: {0}", context.getAuthenticationSession().getParentSession().getId());

        try {
            String appId = UriUtils.getOrigin(context.getUriInfo().getBaseUri());
            RegisterRequestData data = u2f.startRegistration(appId, new LinkedList<>());

            context.getAuthenticationSession().setAuthNote(U2F_REGISTRATION_DATA, data.toJson());

            Response challenge = context.form()
                    .setAttribute("url", new UrlBean(context.getRealm(), context.getSession().theme().getTheme(Theme.Type.LOGIN), context.getSession().getContext().getUri().getBaseUri(), context.getActionUrl()))
                    .setAttribute("request", data)
                    .setAttribute("username", context.getUser().getUsername())
                    .createForm("fido-u2f-register.ftl");

            context.challenge(challenge);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processAction(RequiredActionContext context) {
        logger.debugv("Finish registration, session: {0}", context.getAuthenticationSession().getParentSession().getId());

        try {
            MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
            String username = formData.getFirst("username");
            String tokenResponse = formData.getFirst("tokenResponse");

            RegisterResponse response = RegisterResponse.fromJson(tokenResponse);

            RegisterRequestData data = RegisterRequestData.fromJson(context.getAuthenticationSession().getAuthNote(U2F_REGISTRATION_DATA));

            DeviceRegistration registration = u2f.finishRegistration(data, response);
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
    public void evaluateTriggers(RequiredActionContext context) {
    }

    @Override
    public void close() {
    }

}
