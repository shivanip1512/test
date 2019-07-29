package com.cannontech.database.data.lite;

public class LiteHardwarePAObject extends LiteYukonPAObject {
    
    private Integer inventoryId;

    public LiteHardwarePAObject(LiteYukonPAObject pao, Integer inventoryId) {
        this.setPaoName(pao.getPaoName());
        this.setLiteID(pao.getLiteID());
        this.setPaoType(pao.getPaoType());
        this.inventoryId = inventoryId;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }  

}
