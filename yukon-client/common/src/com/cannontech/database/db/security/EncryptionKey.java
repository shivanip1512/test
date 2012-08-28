package com.cannontech.database.db.security;

public class EncryptionKey {

    private Integer encryptionKeyId = null;
    private String value = null;
    private String name = null;
    private Boolean currentlyUsed = null;
    private Boolean isValid = true;
    
    public EncryptionKey() { }
    
    public EncryptionKey(Integer encryptionKeyId,String name, String value) {
        this.encryptionKeyId = encryptionKeyId;
        this.name = name;
        this.value = value;
    }
    
    public EncryptionKey(Integer encryptionKeyId) {
        this(encryptionKeyId, null, null);
    }
    
    public Integer getEncryptionKeyId() {
        return encryptionKeyId;
    }

    public void setEncryptionKeyId(Integer encryptionKeyId) {
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

    public void setCurrentlyUsed(Boolean currentlyUsed) {
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
