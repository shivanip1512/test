package com.cannontech.web.dr.model;

import org.joda.time.LocalTime;

public class NestSyncSettings {

    private boolean sync;
    private LocalTime syncTime;

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public LocalTime getSyncTime() {
        return syncTime;
    }
    
    public void setSyncTime(LocalTime syncTime) {
        this.syncTime = syncTime;
    }
    
    @Override
    public String toString() {
        return String.format("EcobeeSettings [sync=%s, syncTime=%s]",
                      sync, syncTime);
    }
    
}