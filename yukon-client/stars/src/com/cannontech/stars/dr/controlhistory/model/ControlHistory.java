package com.cannontech.stars.dr.controlhistory.model;

import java.util.List;

import com.cannontech.stars.dr.hardware.model.InventoryBase;

public class ControlHistory {
    private String displayName;
    private InventoryBase inventory;
    private ControlHistoryStatus currentStatus;
    private List<ControlHistoryStatus> controlHistory;
    
    public ControlHistory() {
        
    }
    
    public InventoryBase getInventory() {
        return inventory;
    }
    
    public void setInventory(InventoryBase inventory) {
        this.inventory = inventory;
    }
    
    public ControlHistoryStatus getCurrentStatus() {
        return currentStatus;
    }
    
    public void setCurrentStatus(ControlHistoryStatus currentStatus) {
        this.currentStatus = currentStatus;
    }
    
    public List<ControlHistoryStatus> getControlHistory() {
        return controlHistory;
    }
    
    public void setControlHistory(List<ControlHistoryStatus> controlHistory) {
        this.controlHistory = controlHistory;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((displayName == null) ? 0
                : displayName.hashCode());
        result = prime * result + ((inventory == null) ? 0
                : inventory.hashCode());
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
        final ControlHistory other = (ControlHistory) obj;
        if (displayName == null) {
            if (other.displayName != null)
                return false;
        } else if (!displayName.equals(other.displayName))
            return false;
        if (inventory == null) {
            if (other.inventory != null)
                return false;
        } else if (!inventory.equals(other.inventory))
            return false;
        return true;
    }

}
