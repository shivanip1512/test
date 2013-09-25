package com.cannontech.common.tdc.model;

public class Cog {  
    private boolean manualEntry;
    private boolean manualControl;
    private boolean tags;
    private boolean enableDisable;
    private boolean trend;
    private boolean altScan;
    
    public boolean isTags() {
        return tags;
    }
    public void setTags(boolean tags) {
        this.tags = tags;
    }
    public boolean isEnableDisable() {
        return enableDisable;
    }
    public void setEnableDisable(boolean enableDisable) {
        this.enableDisable = enableDisable;
    }
    public boolean isTrend() {
        return trend;
    }
    public void setTrend(boolean trend) {
        this.trend = trend;
    }
    public boolean isAltScan() {
        return altScan;
    }
    public void setAltScan(boolean altScan) {
        this.altScan = altScan;
    }
    public boolean isManualEntry() {
        return manualEntry;
    }
    public void setManualEntry(boolean manualEntry) {
        this.manualEntry = manualEntry;
    }
    public boolean isManualControl() {
        return manualControl;
    }
    public void setManualControl(boolean manualControl) {
        this.manualControl = manualControl;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (altScan ? 1231 : 1237);
        result = prime * result + (enableDisable ? 1231 : 1237);
        result = prime * result + (manualControl ? 1231 : 1237);
        result = prime * result + (manualEntry ? 1231 : 1237);
        result = prime * result + (tags ? 1231 : 1237);
        result = prime * result + (trend ? 1231 : 1237);
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
        Cog other = (Cog) obj;
        if (altScan != other.altScan)
            return false;
        if (enableDisable != other.enableDisable)
            return false;
        if (manualControl != other.manualControl)
            return false;
        if (manualEntry != other.manualEntry)
            return false;
        if (tags != other.tags)
            return false;
        if (trend != other.trend)
            return false;
        return true;
    }
    
    
}
