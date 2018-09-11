package org.keycloak.experimental.dummysign;

import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.keys.KeyProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

public class DummyKeyProviderFactory implements KeyProviderFactory<DummyKeyProvider> {

    @Override
    public DummyKeyProvider create(KeycloakSession session, ComponentModel model) {
        return new DummyKeyProvider(model);
    }

    @Override
    public String getHelpText() {
        return "Dummy key provider";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property("name", "Name", "Name", ProviderConfigProperty.STRING_TYPE, null, null).build();
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
        return "dummy-key-provider";
    }
}
