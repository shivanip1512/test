package com.cannontech.database.data.lite;

public class LiteEatonCloudPAObject extends LiteYukonPAObject {

    private String guid;
    
    public LiteEatonCloudPAObject(LiteYukonPAObject pao, String guid) {
        this.setPaoName(pao.getPaoName());
        this.setLiteID(pao.getLiteID());
        this.setPaoType(pao.getPaoType());
        this.guid = guid;
    }
    
    public void setGuid(String guid) {
        this.guid = guid;
    }
    
    public String getGuid() {
        return this.guid;
    }

}
