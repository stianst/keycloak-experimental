package org.keycloak.summit;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.wellknown.WellKnownProvider;
import org.keycloak.wellknown.WellKnownProviderFactory;

public class ClusterWellKnownInfoFactory implements WellKnownProviderFactory {
    @Override
    public WellKnownProvider create(KeycloakSession session) {
        return new ClusterWellKnownInfo(session);
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
        return "cluster";
    }
}
