package com.cannontech.encryption;

public class ItronSecurityKeyPair {
    
    private String privateKey;
    private String publicKey;
    private boolean privateKeyEncrypted;
    
    public ItronSecurityKeyPair() {
        this.privateKey = null;
        this.publicKey = null;
        this.privateKeyEncrypted = false;
    }

    public ItronSecurityKeyPair(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }
    
    public ItronSecurityKeyPair(String privateKey, String publicKey, boolean privateKeyEncrypted) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.privateKeyEncrypted = privateKeyEncrypted;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public boolean isPrivateKeyEncrypted() {
        return privateKeyEncrypted;
    }

    public void setPrivateKeyEncrypted(boolean privateKeyEncrypted) {
        this.privateKeyEncrypted = privateKeyEncrypted;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((privateKey == null) ? 0 : privateKey.hashCode());
        result = prime * result + (privateKeyEncrypted ? 1231 : 1237);
        result = prime * result + ((publicKey == null) ? 0 : publicKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ItronSecurityKeyPair other = (ItronSecurityKeyPair) obj;
        if (privateKey == null) {
            if (other.privateKey != null)
                return false;
        } else if (!privateKey.equals(other.privateKey))
            return false;
        if (privateKeyEncrypted != other.privateKeyEncrypted)
            return false;
        if (publicKey == null) {
            if (other.publicKey != null)
                return false;
        } else if (!publicKey.equals(other.publicKey))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ItronSecurityKeyPair [privateKey=" + privateKey + ", publicKey=" + publicKey
               + ", privateKeyEncrypted=" + privateKeyEncrypted + "]";
    }
}
