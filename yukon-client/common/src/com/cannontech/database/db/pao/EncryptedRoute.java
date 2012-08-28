package com.cannontech.database.db.pao;

import com.cannontech.common.pao.PaoType;

public class EncryptedRoute {

    private String paoName;
    private PaoType type;
    private Integer paobjectId;
    private String encryptionKeyName;
    private Integer encryptionKeyId;

    public boolean isEncrypted() {
        return (encryptionKeyId != null);
    }

    public Integer getPaobjectId() {
        return paobjectId;
    }

    public void setPaobjectId(Integer paobjectId) {
        this.paobjectId = paobjectId;
    }

    public String getPaoName() {
        return paoName;
    }

    public void setPaoName(String paoName) {
        this.paoName = paoName;
    }

    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }

    public String getEncryptionKeyName() {
        return encryptionKeyName;
    }

    public void setEncryptionKeyName(String encryptionKeyName) {
        this.encryptionKeyName = encryptionKeyName;
    }

    public Integer getEncryptionKeyId() {
        return encryptionKeyId;
    }

    public void setEncryptionKeyId(Integer encryptionKeyId) {
        this.encryptionKeyId = encryptionKeyId;
    }

}