package com.cannontech.web.admin.encryption;

public class EncryptedRoute {
    
    private int paobjectId;
    private String paoName;
    private String type;
    private String value;
    private String infoKey;
    
    public boolean isEnabled() {
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
    public String getType() {
        return type;
    }
    public void setType(String type) {
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