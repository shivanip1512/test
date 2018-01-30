package com.cannontech.infrastructure.model;

import java.io.Serializable;

import org.joda.time.Instant;

/**
 * This request is sent to initiate infrastructure warnings cache refresh.
 */
public class InfrastructureWarningsRefreshRequest implements Serializable {
    private Instant lastRunTime;
    private Instant nextRunTime;
    
    public Instant getLastRunTime() {
        return lastRunTime;
    }
    public void setLastRunTime(Instant lastRunTime) {
        this.lastRunTime = lastRunTime;
    }
    public Instant getNextRunTime() {
        return nextRunTime;
    }
    public void setNextRunTime(Instant nextRunTime) {
        this.nextRunTime = nextRunTime;
    }
}
