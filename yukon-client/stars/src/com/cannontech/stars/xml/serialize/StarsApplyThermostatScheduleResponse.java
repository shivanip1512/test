package com.cannontech.stars.xml.serialize;

public class StarsApplyThermostatScheduleResponse {
    private int inventoryID;
    private boolean hasInventoryID;
    private StarsThermostatProgram starsThermostatProgram;

    public StarsApplyThermostatScheduleResponse() {
        
    } 

    public void deleteInventoryID() {
        this.hasInventoryID = false;
    } 

    public int getInventoryID() {
        return this.inventoryID;
    }

    public StarsThermostatProgram getStarsThermostatProgram() {
        return this.starsThermostatProgram;
    }

    public boolean hasInventoryID() {
        return this.hasInventoryID;
    }

    public void setInventoryID(int inventoryID) {
        this.inventoryID = inventoryID;
        this.hasInventoryID = true;
    }

    public void setStarsThermostatProgram(StarsThermostatProgram starsThermostatProgram) {
        this.starsThermostatProgram = starsThermostatProgram;
    }

}
