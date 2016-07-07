package com.cannontech.web.stars.dr.operator.inventory.model;

public class ShedLoad {

    private int inventoryId;
    private int relayNo;
    private String duration;
   
    public ShedLoad() {
    }
    
    public ShedLoad(int inventoryId) {
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
