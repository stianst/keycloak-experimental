package org.keycloak.experimental.u2f;

import org.keycloak.models.KeycloakSession;
import org.keycloak.theme.ThemeResourceProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class U2FThemeResourceProvider implements ThemeResourceProvider {

    private KeycloakSession session;

    public U2FThemeResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public URL getTemplate(String name) throws IOException {
        if (name.equals("fido-u2f-login.ftl") || name.equals("fido-u2f-register.ftl")) {
            return U2FThemeResourceProvider.class.getResource(name);
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String path) throws IOException {
        if (path.equals("u2f-api-1.1.js")) {
            return U2FThemeResourceProvider.class.getResourceAsStream(path);
        }
        return null;
    }

    @Override
    public void close() {
    }

}
