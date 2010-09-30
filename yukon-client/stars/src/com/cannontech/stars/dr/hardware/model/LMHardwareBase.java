package com.cannontech.stars.dr.hardware.model;

public class LMHardwareBase {
    private int inventoryId;
    private String manufacturerSerialNumber;
    private int LMHarewareTypeId;
    private int routeId;
    private int configurationId;
    
    public LMHardwareBase() {
        
    }

    public int getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(int configurationId) {
        this.configurationId = configurationId;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getLMHarewareTypeId() {
        return LMHarewareTypeId;
    }

    public void setLMHarewareTypeId(int harewareTypeId) {
        LMHarewareTypeId = harewareTypeId;
    }
    
    public String getManufacturerSerialNumber() {
        return manufacturerSerialNumber;
    }
    
    public void setManufacturerSerialNumber(String manufacturerSerialNumber) {
        this.manufacturerSerialNumber = manufacturerSerialNumber;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + LMHarewareTypeId;
        result = PRIME * result + configurationId;
        result = PRIME * result + inventoryId;
        result = PRIME * result + ((manufacturerSerialNumber == null) ? 0 : manufacturerSerialNumber.hashCode());
        result = PRIME * result + routeId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final LMHardwareBase other = (LMHardwareBase) obj;
        if (LMHarewareTypeId != other.LMHarewareTypeId)
            return false;
        if (configurationId != other.configurationId)
            return false;
        if (inventoryId != other.inventoryId)
            return false;
        if (manufacturerSerialNumber == null) {
            if (other.manufacturerSerialNumber != null)
                return false;
        } else if (!manufacturerSerialNumber.equals(other.manufacturerSerialNumber))
            return false;
        if (routeId != other.routeId)
            return false;
        return true;
    }

}
