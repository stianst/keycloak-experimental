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

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class MagicLinkFormAuthenticator extends AbstractUsernameFormAuthenticator implements Authenticator {

    private static final Logger logger = Logger.getLogger(MagicLinkFormAuthenticator.class);

    @Override
    public void action(AuthenticationFlowContext context) {
        logger.debugv("Finish signature, session: {0}", context.getAuthenticationSession().getParentSession().getId());

        try {
            // TODO
            context.success();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        logger.debugv("Sending challenge, session: {0}", context.getAuthenticationSession().getParentSession().getId());

        try {
            // TODO
            context.challenge(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        // TODO
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // TODO
    }

    @Override
    public void close() {
    }
}
