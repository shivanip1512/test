package com.cannontech.amr.archivedValueExporter.model.dataRange;

public class SinceLastChangeId {
    private long firstChangeId;
    private long lastChangeId;
    
    public long getFirstChangeId() {
        return firstChangeId;
    }
    public void setFirstChangeId(long firstChangeId) {
        this.firstChangeId = firstChangeId;
    }
    
    public long getLastChangeId() {
        return lastChangeId;
    }
    public void setLastChangeId(long lastChangeId) {
        this.lastChangeId = lastChangeId;
    }
}