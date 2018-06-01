package org.keycloak.summit;

import org.keycloak.models.KeycloakSession;
import org.keycloak.wellknown.WellKnownProvider;
import org.keycloak.wellknown.WellKnownProviderFactory;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class ClusterWellKnownInfo implements WellKnownProvider {

    private KeycloakSession session;

    public ClusterWellKnownInfo(KeycloakSession session) {

        this.session = session;
    }

    @Override
    public void close() {
    }

    @Override
    public Object getConfig() {
        Map<String, String> m = new HashMap<>();

        try {
            m.put("contextAuthServerUrl", session.getContext().getAuthServerUrl().toURL().toString());
            m.put("contextRemoteAddr", session.getContext().getConnection().getRemoteAddr());
            m.put("headerXForwardedHost", session.getContext().getRequestHeaders().getHeaderString("X-Forwarded-Host"));
            m.put("headerXForwardedFor", session.getContext().getRequestHeaders().getHeaderString("X-Forwarded-For"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return m;
    }
}
