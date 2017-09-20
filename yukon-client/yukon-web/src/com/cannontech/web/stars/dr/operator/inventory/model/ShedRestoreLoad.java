package com.cannontech.web.stars.dr.operator.inventory.model;

import org.joda.time.Minutes;

public class ShedRestoreLoad {

    private int inventoryId;
    private int relayNo;
    private int duration = Minutes.minutes(5).toStandardSeconds().getSeconds();
    
    public ShedRestoreLoad() {
    }
    
    public ShedRestoreLoad(int inventoryId) {
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
