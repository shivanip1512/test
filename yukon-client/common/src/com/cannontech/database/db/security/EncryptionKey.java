package com.cannontech.database.db.security;

public class EncryptionKey {

    private int encryptionKeyId;
    private String value = null;
    private String name = null;
    private boolean currentlyUsed;
    private boolean isValid = true;
    
    public EncryptionKey() { }
    
    public EncryptionKey(Integer encryptionKeyId, String name, String value, boolean currentlyUsed) {
        this.encryptionKeyId = encryptionKeyId;
        this.name = name;
        this.value = value;
        this.currentlyUsed = currentlyUsed;
    }
    
    public Integer getEncryptionKeyId() {
        return encryptionKeyId;
    }

    public void setEncryptionKeyId(int encryptionKeyId) {
        this.encryptionKeyId = encryptionKeyId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
}
