package com.cannontech.web.dev;

public class PGPKeyPair {
    private String pgpPrivateKey;
    private String pgpPublicKey;

    public String getPgpPrivateKey() {
        return pgpPrivateKey;
    }

    public void setPgpPrivateKey(String pgpPrivateKey) {
        this.pgpPrivateKey = pgpPrivateKey;
    }

    public String getPgpPublicKey() {
        return pgpPublicKey;
    }

    public void setPgpPublicKey(String pgpPublicKey) {
        this.pgpPublicKey = pgpPublicKey;
    }

}
