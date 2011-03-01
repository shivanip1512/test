package com.cannontech.stars.dr.hardware.model;

public class SwitchAssignment {

    private boolean assigned;
    private int inventoryId;
    private String serialNumber;
    private String label;
    
    public boolean isAssigned() {
        return assigned;
    }
    
    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public int getInventoryId() {
        return inventoryId;
    }
    
    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
}