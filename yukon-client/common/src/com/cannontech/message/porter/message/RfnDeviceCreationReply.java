package com.cannontech.message.porter.message;

public class RfnDeviceCreationReply {
    private int paoId;
    private String category, deviceType;
    
    public RfnDeviceCreationReply(int paoId, String category, String deviceType) {
        this.paoId = paoId;
        this.category = category;
        this.deviceType = deviceType;
    }
    
    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public RfnDeviceCreationReply() {}

    public int getPaoId() {
        return paoId;
    }
    
    public String getCategory() {
        return category;
    }
    
    public String getDeviceType() {
        return deviceType;
    }
}
