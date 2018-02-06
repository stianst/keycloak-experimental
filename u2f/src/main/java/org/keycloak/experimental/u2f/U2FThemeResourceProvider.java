package org.keycloak.experimental.u2f;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.theme.ThemeResourceProvider;
import org.keycloak.theme.ThemeResourceProviderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class U2FThemeResourceProvider implements ThemeResourceProvider, ThemeResourceProviderFactory {

    @Override
    public ThemeResourceProvider create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "u2f-resources";
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

}
