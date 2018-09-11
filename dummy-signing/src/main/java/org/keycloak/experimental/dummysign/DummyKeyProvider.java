package org.keycloak.experimental.dummysign;

import org.keycloak.component.ComponentModel;
import org.keycloak.jose.jws.AlgorithmType;
import org.keycloak.keys.Attributes;
import org.keycloak.keys.KeyMetadata;
import org.keycloak.keys.KeyProvider;
import org.keycloak.keys.SignatureKeyProvider;

import java.security.Key;
import java.util.LinkedList;
import java.util.List;

public class DummyKeyProvider implements KeyProvider, SignatureKeyProvider {

    public static final String KEY_TYPE = "DUMMY";
    private ComponentModel model;

    public DummyKeyProvider(ComponentModel model) {
        this.model = model;
    }

    // Need to be able to introduce new algorithm types
    @Override
    public AlgorithmType getType() {
        return AlgorithmType.ECDSA;
    }

    @Override
    public String getKid() {
        return model.get("name");
    }

    @Override
    public List getKeyMetadata() {
        List l = new LinkedList();

        DummyKeyMetadata k = new DummyKeyMetadata();
        k.setProviderId(model.getId());
        k.setProviderPriority(model.get(Attributes.PRIORITY_KEY, 0l));
        k.setKid(model.get("name"));
        k.setStatus(KeyMetadata.Status.ACTIVE);
        l.add(k);

        return l;
    }

    @Override
    public String getKeyType() {
        return KEY_TYPE;
    }

    @Override
    public void close() {
    }

    @Override
    public Key getSignKey() {
        return new DummyKey(model.get("name"));
    }

    @Override
    public Key getVerifyKey(String kid) {
        return new DummyKey(model.get(kid));
    }

    public static class DummyKey implements Key {

        private String name;

        public DummyKey(String name) {
            this.name = name;
        }

        @Override
        public String getAlgorithm() {
            return AlgorithmType.ECDSA.name();
        }

        @Override
        public String getFormat() {
            return null;
        }

        @Override
        public byte[] getEncoded() {
            return name.getBytes();
        }

        public String getName() {
            return name;
        }
    }

}
