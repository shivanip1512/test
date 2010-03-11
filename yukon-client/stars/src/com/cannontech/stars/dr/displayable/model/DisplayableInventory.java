package com.cannontech.stars.dr.displayable.model;

import java.util.List;

import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.program.model.Program;

public class DisplayableInventory {
    private int inventoryId;
    private String displayName;
    private String serialNumber;
    private List<Program> programs;
    private boolean currentlyOptedOut = false;
    private OptOutEvent currentlyScheduledOptOut;

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
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public List<Program> getPrograms() {
        return programs;
    }
    
    public void setPrograms(List<Program> programs) {
        this.programs = programs;
    }

    public boolean isCurrentlyOptedOut() {
        return currentlyOptedOut;
    }

    public void setCurrentlyOptedOut(boolean currentlyOptedOut) {
        this.currentlyOptedOut = currentlyOptedOut;
    }

    public OptOutEvent getCurrentlyScheduledOptOut() {
        return currentlyScheduledOptOut;
    }
    
    public void setCurrentlyScheduledOptOut(OptOutEvent currentlyScheduledOptOut) {
        this.currentlyScheduledOptOut = currentlyScheduledOptOut;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((displayName == null) ? 0
                : displayName.hashCode());
        result = prime * result + inventoryId;
        result = prime * result + ((programs == null) ? 0 : programs.hashCode());
        result = prime * result + ((serialNumber == null) ? 0
                : serialNumber.hashCode());
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
        final DisplayableInventory other = (DisplayableInventory) obj;
        if (displayName == null) {
            if (other.displayName != null)
                return false;
        } else if (!displayName.equals(other.displayName))
            return false;
        if (inventoryId != other.inventoryId)
            return false;
        if (programs == null) {
            if (other.programs != null)
                return false;
        } else if (!programs.equals(other.programs))
            return false;
        if (serialNumber == null) {
            if (other.serialNumber != null)
                return false;
        } else if (!serialNumber.equals(other.serialNumber))
            return false;
        return true;
    }

}
