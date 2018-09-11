package org.keycloak.experimental.dummysign;

import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.jose.jws.TokenSignatureProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Collections;
import java.util.List;

public class DummyTokenSignatureProviderFactory implements TokenSignatureProviderFactory<DummyTokenSignatureProvider> {

    @Override
    public DummyTokenSignatureProvider create(KeycloakSession session, ComponentModel model) {
        return new DummyTokenSignatureProvider();
    }

    @Override
    public String getHelpText() {
        return "Dummy signatures";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
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
        return "dummy-signature";
    }

}
