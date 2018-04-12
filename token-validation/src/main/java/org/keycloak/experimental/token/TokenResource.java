package org.keycloak.experimental.token;

import org.keycloak.OAuthErrorException;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.forms.login.freemarker.model.UrlBean;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.TokenManager;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.Urls;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.theme.FreeMarkerException;
import org.keycloak.theme.FreeMarkerUtil;
import org.keycloak.theme.Theme;
import org.keycloak.util.JsonSerialization;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class TokenResource {

    private KeycloakSession session;
    private final Theme theme;
    private final FreeMarkerUtil freemarker;

    public TokenResource(KeycloakSession session) {
        try {
            this.session = session;
            theme = session.theme().getTheme(Theme.Type.LOGIN);
            freemarker = new FreeMarkerUtil();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    public Response getForm() throws IOException, FreeMarkerException {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("properties", theme.getProperties());
        attributes.put("url", new UrlBean(session.getContext().getRealm(), theme, session.getContext().getAuthServerUrl(), null));

        String response = freemarker.processTemplate(attributes, "token-validator.ftl", theme);
        return Response.ok(response).build();
    }

    @POST
    public Response parseToken(@FormParam("token") String token) throws IOException, FreeMarkerException {
        token = token.trim();

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("properties", theme.getProperties());
        attributes.put("url", new UrlBean(session.getContext().getRealm(), theme, session.getContext().getAuthServerUrl(), null));

        try {
            TokenVerifier<AccessToken> verifier = TokenVerifier.create(token, AccessToken.class).withChecks(
                    TokenVerifier.IS_ACTIVE,
                    new TokenVerifier.RealmUrlCheck(Urls.realmIssuer(session.getContext().getAuthServerUrl(), session.getContext().getRealm().getName()))
            );

            attributes.put("token", token);
            attributes.put("header", JsonSerialization.writeValueAsPrettyString(verifier.getHeader()));
            attributes.put("tokenParsed", JsonSerialization.writeValueAsPrettyString(verifier.getToken()));

            String kid = verifier.getHeader().getKeyId();

            PublicKey publicKey = session.keys().getRsaPublicKey(session.getContext().getRealm(), kid);
            verifier.publicKey(publicKey);

            String activeKid = session.keys().getActiveRsaKey(session.getContext().getRealm()).getKid();

            if (publicKey != null) {
                verifier.verify();

                UserSessionModel userSession = session.sessions().getUserSession(session.getContext().getRealm(), verifier.getToken().getSessionState());
                if (!AuthenticationManager.isSessionValid(session.getContext().getRealm(), userSession)) {
                    throw new Exception("Session not active");
                }

                UserModel user = userSession.getUser();
                if (user == null) {
                    throw new Exception("Unknown user");
                }

                if (!user.isEnabled()) {
                    throw new Exception("User disabled");
                }

                ClientModel client = session.getContext().getRealm().getClientByClientId(verifier.getToken().getIssuedFor());

                if (verifier.getToken().getIssuedAt() < client.getNotBefore()) {
                    throw new Exception("Stale token");
                }
                if (verifier.getToken().getIssuedAt() < session.getContext().getRealm().getNotBefore()) {
                    throw new Exception("Stale token");
                }
                if (verifier.getToken().getIssuedAt() < session.users().getNotBeforeOfUser(session.getContext().getRealm(), user)) {
                    throw new Exception("Stale token");
                }

                attributes.put("valid", Boolean.TRUE);
                attributes.put("activeKey", activeKid.equals(kid));
            } else {
                attributes.put("valid", Boolean.FALSE);
                attributes.put("error", "Key not found");
            }

        } catch (Exception e) {
            attributes.put("valid", Boolean.FALSE);
            attributes.put("error", e.getMessage());
        }

        return Response.ok(freemarker.processTemplate(attributes, "token-validator.ftl", theme)).build();
    }

}
