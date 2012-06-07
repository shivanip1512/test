package com.cannontech.stars.dr.hardware.model;

public class LMHardwareConfiguration {
    private int inventoryId;
    private int applianceId;
    private int addressingGroupId;
    private int loadNumber;
    
    public LMHardwareConfiguration() { 
    }
    
    public LMHardwareConfiguration(int inventoryId, int appId, int addressingGroupId, int relay) { 
        this.inventoryId = inventoryId;
        this.applianceId = appId;
        this.addressingGroupId = addressingGroupId;
        this.loadNumber = relay;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getApplianceId() {
        return applianceId;
    }

    public void setApplianceId(int appId) {
        applianceId = appId;
    }

    public int getAddressingGroupId() {
        return addressingGroupId;
    }

    public void setAddressingGroupId(int loadGroupId) {
        this.addressingGroupId = loadGroupId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final LMHardwareConfiguration other = (LMHardwareConfiguration) obj;
        if (inventoryId != other.inventoryId)
            return false;
        if (applianceId != other.applianceId)
            return false;
        if (addressingGroupId != other.addressingGroupId)
            return false;
        if (loadNumber != other.loadNumber)
            return false;
        return true;
    }

    public int getLoadNumber() {
        return loadNumber;
    }

    public void setLoadNumber(int relay) {
        this.loadNumber = relay;
    }
}
