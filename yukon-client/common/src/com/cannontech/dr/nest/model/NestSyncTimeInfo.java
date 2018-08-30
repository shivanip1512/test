package com.cannontech.dr.nest.model;

import org.joda.time.Instant;

public class NestSyncTimeInfo {
    private Instant syncTime;
    private Instant nextSyncTime;
    private boolean syncInProgress;

    public Instant getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Instant syncTime) {
        this.syncTime = syncTime;
    }

    public Instant getNextSyncTime() {
        return nextSyncTime;
    }

    public void setNextSyncTime(Instant nextSyncTime) {
        this.nextSyncTime = nextSyncTime;
    }

    public boolean isSyncInProgress() {
        return syncInProgress;
    }

    public void setSyncInProgress(boolean syncInProgress) {
        this.syncInProgress = syncInProgress;
    }
    
    public boolean enableSyncButton() {
        if (syncInProgress) {
            return false;
        }
        if (nextSyncTime == null || nextSyncTime.isBeforeNow()) {
            return true;
        }
        return false;
    }
}
