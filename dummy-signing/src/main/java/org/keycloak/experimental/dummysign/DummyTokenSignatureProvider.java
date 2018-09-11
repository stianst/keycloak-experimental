package org.keycloak.experimental.dummysign;

import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.TokenSignatureProvider;

import java.security.Key;

public class DummyTokenSignatureProvider implements TokenSignatureProvider {

    @Override
    public byte[] sign(byte[] data, String sigAlgName, Key k) {
        DummyKeyProvider.DummyKey key = (DummyKeyProvider.DummyKey) k;
        return key.getName().getBytes();
    }

    @Override
    public boolean verify(JWSInput jws, Key k) {
        DummyKeyProvider.DummyKey key = (DummyKeyProvider.DummyKey) k;
        return jws.getEncodedSignature().equals(key.getName().getBytes());
    }

    @Override
    public String getRequiredKeyType() {
        return DummyKeyProvider.KEY_TYPE;
    }

    @Override
    public void close() {
    }

}
