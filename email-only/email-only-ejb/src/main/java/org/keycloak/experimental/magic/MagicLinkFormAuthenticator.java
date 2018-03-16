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

package org.keycloak.experimental.magic;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.common.util.Base64Url;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.models.utils.KeycloakModelUtils;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class MagicLinkFormAuthenticator extends AbstractUsernameFormAuthenticator implements Authenticator {

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String email = formData.getFirst("email");
        String code = formData.getFirst("code");

        if (email != null) {
            if (email.isEmpty() || !email.contains("@")) {
                context.challenge(context.form().addError(new FormMessage("Invalid email")).createForm("login-email-only.ftl"));
                return;
            }
            UserModel user = context.getSession().users().getUserByEmail(email, context.getRealm());
            if (user == null) {
                // Register user
                user = context.getSession().users().addUser(context.getRealm(), email);
                user.setEnabled(true);
                user.setEmail(email);

                context.setUser(user);

                context.success();
            } else {
                String key = Base64Url.encode(KeycloakModelUtils.generateSecret(4));
                context.getAuthenticationSession().setAuthNote("email-key", key);

                String body = "Login code: " + key;
                try {
                    context.getSession().getProvider(EmailSenderProvider.class).send(context.getRealm().getSmtpConfig(), user, "Login link", null, body);
                } catch (EmailException e) {
                    e.printStackTrace();
                }

                context.setUser(user);
                context.challenge(context.form().createForm("view-email.ftl"));
            }
        } else if (code != null) {
            String sessionKey = context.getAuthenticationSession().getAuthNote("email-key");
            if (sessionKey.equals(code)) {
                context.success();
            }
        } else {
            context.form().createErrorPage(Response.Status.INTERNAL_SERVER_ERROR);
            return;
        }
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        context.challenge(context.form().createForm("login-email-only.ftl"));
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public void close() {
    }

}
