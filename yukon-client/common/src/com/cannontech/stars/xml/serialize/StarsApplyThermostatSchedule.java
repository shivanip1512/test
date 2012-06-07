package com.cannontech.stars.xml.serialize;

public class StarsApplyThermostatSchedule {
    private int inventoryID;
    private boolean hasInventoryID;
    private int scheduleID;
    private boolean hasScheduleID;

    public StarsApplyThermostatSchedule() {
        
    }

    public void deleteInventoryID() {
        this.hasInventoryID = false;
    } 

    public void deleteScheduleID() {
        this.hasScheduleID = false;
    } 

    public int getInventoryID() {
        return this.inventoryID;
    }

    public int getScheduleID() {
        return this.scheduleID;
    }

    public boolean hasInventoryID() {
        return this.hasInventoryID;
    }

    public boolean hasScheduleID() {
        return this.hasScheduleID;
    }

    public void setInventoryID(int inventoryID) {
        this.inventoryID = inventoryID;
        this.hasInventoryID = true;
    }

    public void setScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
        this.hasScheduleID = true;
    }

}
