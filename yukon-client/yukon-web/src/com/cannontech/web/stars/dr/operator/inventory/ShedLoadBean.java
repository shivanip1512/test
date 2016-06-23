package com.cannontech.web.stars.dr.operator.inventory;

public class ShedLoadBean {

    private int inventoryId;
    private int relayNo;
    private int duration;
   
    public ShedLoadBean() {
    }
    
    public ShedLoadBean(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getRelayNo() {
        return relayNo;
    }

    public void setRelayNo(int relayNo) {
        this.relayNo = relayNo;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
