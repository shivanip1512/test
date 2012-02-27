package com.cannontech.common.inventory;

import java.io.Serializable;

import org.apache.commons.lang.Validate;

public class InventoryIdentifier implements YukonInventory, Serializable {

    private int inventoryId;
    private HardwareType hardwareType;
    
    public InventoryIdentifier(int inventoryId, HardwareType hardwareType) {
        super();
        Validate.notNull(hardwareType, "hardwareType must not be null");
        this.inventoryId = inventoryId;
        this.hardwareType = hardwareType;
    }
    
    public int getInventoryId() {
        return inventoryId;
    }
    
    public HardwareType getHardwareType() {
        return hardwareType;
    }
    
    @Override
    public InventoryIdentifier getInventoryIdentifier() {
        return this;
    }
    
    @Override
    public String toString() {
        return hardwareType + " : " + inventoryId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + inventoryId;
        result = prime * result + ((hardwareType == null) ? 0 : hardwareType.hashCode());
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
        InventoryIdentifier other = (InventoryIdentifier) obj;
        if (hardwareType == null) {
            if (other.hardwareType != null)
                return false;
        } else if (!hardwareType.equals(other.hardwareType))
            return false;
        if (inventoryId != other.inventoryId)
            return false;
        return true;
    }

}