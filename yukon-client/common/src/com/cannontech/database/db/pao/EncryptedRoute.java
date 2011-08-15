package com.cannontech.database.db.pao;

import com.cannontech.common.pao.PaoType;

public class EncryptedRoute {

    private int paobjectId;
    private String paoName;
    private PaoType type;
    private String value;
    private String infoKey;

    public boolean isEncrypted() {
        return (value != null);
    }

    public int getPaobjectId() {
        return paobjectId;
    }

    public void setPaobjectId(int paobjectId) {
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getInfoKey() {
        return infoKey;
    }

    public void setInfoKey(String paoInfo) {
        this.infoKey = paoInfo;
    }

}