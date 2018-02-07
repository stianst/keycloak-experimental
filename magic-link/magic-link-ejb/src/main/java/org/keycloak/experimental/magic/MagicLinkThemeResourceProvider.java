package org.keycloak.experimental.magic;

import org.keycloak.models.KeycloakSession;
import org.keycloak.theme.ThemeResourceProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MagicLinkThemeResourceProvider implements ThemeResourceProvider {

    private KeycloakSession session;

    public MagicLinkThemeResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public URL getTemplate(String name) throws IOException {
        if (name.equals("magic-link-login.ftl")) {
            return MagicLinkThemeResourceProvider.class.getResource(name);
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String path) throws IOException {
        return null;
    }

    @Override
    public void close() {
    }

}
