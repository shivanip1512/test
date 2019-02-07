package com.cannontech.database.db.security;

import org.joda.time.Instant;

public class EncryptionKey {

    private int encryptionKeyId;
    private String privateKey;
    private String publicKey;
    private String name = null;
    private boolean currentlyUsed;
    private boolean isValid = true;
    private Instant timestamp;
    
    public EncryptionKey() { }
    
    public EncryptionKey(Integer encryptionKeyId, String name, String privateKey, boolean currentlyUsed) {
        this.encryptionKeyId = encryptionKeyId;
        this.name = name;
        this.privateKey = privateKey;
        this.currentlyUsed = currentlyUsed;
    }
    
    public EncryptionKey(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public Integer getEncryptionKeyId() {
        return encryptionKeyId;
    }

    public void setEncryptionKeyId(int encryptionKeyId) {
        this.encryptionKeyId = encryptionKeyId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrentlyUsed(boolean currentlyUsed) {
        this.currentlyUsed = currentlyUsed;
    }

    public Boolean getCurrentlyUsed() {
        return currentlyUsed;
    }
    
    public Boolean getIsValid() {
        return isValid;
    }
    
    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
