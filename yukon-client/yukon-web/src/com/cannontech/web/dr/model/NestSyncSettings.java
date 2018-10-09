package com.cannontech.web.dr.model;

import org.joda.time.LocalTime;

public class NestSyncSettings {

    private boolean sync;
    private LocalTime scheduledSyncTime;
    
    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public LocalTime getScheduledSyncTime() {
        return scheduledSyncTime;
    }

    public void setScheduledSyncTime(LocalTime scheduledSyncTime) {
        this.scheduledSyncTime = scheduledSyncTime;
    }

    @Override
    public String toString() {
        return String.format("NestSyncSettings [sync=%s, scheduledSyncTime=%s]",
                      sync, scheduledSyncTime);
    }
    
}