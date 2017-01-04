package com.cannontech.encryption;

public class SecurityKeyPair {
    private final String privateKey;
    private final String publicKey;

    public SecurityKeyPair(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }
}
