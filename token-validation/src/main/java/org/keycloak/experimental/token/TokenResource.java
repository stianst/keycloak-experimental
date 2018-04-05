package org.keycloak.experimental.token;

import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.AccessToken;
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
        String response = freemarker.processTemplate(null, "token-validator.ftl", theme);
        return Response.ok(response).build();
    }

    @POST
    public Response parseToken(@FormParam("token") String token) throws FreeMarkerException {
        token = token.trim();

        Map<String, Object> attributes = new HashMap<String, Object>();

        try {
            TokenVerifier<AccessToken> verifier = TokenVerifier.create(token, AccessToken.class);

            attributes.put("token", token);
            attributes.put("header", JsonSerialization.writeValueAsPrettyString(verifier.getHeader()));
            attributes.put("tokenParsed", JsonSerialization.writeValueAsPrettyString(verifier.getToken()));

            String kid = verifier.getHeader().getKeyId();

            PublicKey publicKey = session.keys().getRsaPublicKey(session.getContext().getRealm(), kid);
            verifier.publicKey(publicKey);

            String activeKid = session.keys().getActiveRsaKey(session.getContext().getRealm()).getKid();

            if (publicKey != null) {
                verifier.verify();

                attributes.put("valid", Boolean.TRUE);
                attributes.put("activeKey", activeKid.equals(kid));
            } else {
                attributes.put("valid", Boolean.FALSE);
                attributes.put("error", "Key not found");
            }

        } catch (VerificationException e) {
            attributes.put("valid", Boolean.FALSE);
            attributes.put("error", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Response.ok(freemarker.processTemplate(attributes, "token-validator.ftl", theme)).build();
    }

}
