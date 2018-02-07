package org.keycloak.experimental.magic;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.theme.ThemeResourceProvider;
import org.keycloak.theme.ThemeResourceProviderFactory;

public class MagicLinkThemeResourceProviderFactory implements ThemeResourceProviderFactory {

    public static final String ID = "magic-resources";

    @Override
    public ThemeResourceProvider create(KeycloakSession session) {
        return new MagicLinkThemeResourceProvider(session);
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
