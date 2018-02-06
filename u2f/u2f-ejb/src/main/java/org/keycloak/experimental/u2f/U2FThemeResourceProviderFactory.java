package org.keycloak.experimental.u2f;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.theme.ThemeResourceProvider;
import org.keycloak.theme.ThemeResourceProviderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class U2FThemeResourceProviderFactory implements ThemeResourceProviderFactory {

    public static final String ID = "u2f-resources";

    @Override
    public ThemeResourceProvider create(KeycloakSession session) {
        return new U2FThemeResourceProvider(session);
    }

    @Override
    public String getId() {
        return ID;
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

}
