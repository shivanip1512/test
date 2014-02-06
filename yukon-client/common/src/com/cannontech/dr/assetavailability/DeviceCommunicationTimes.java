package com.cannontech.dr.assetavailability;

import org.joda.time.Instant;

public class DeviceCommunicationTimes {
    private final Instant lastCommunicationTime;
    private final Instant lastNonZeroRuntime;
    
    public DeviceCommunicationTimes(Instant lastCommunicationTime, Instant lastNonZeroRuntime) {
        this.lastCommunicationTime = lastCommunicationTime;
        this.lastNonZeroRuntime = lastNonZeroRuntime;
    }

    public Instant getLastCommunicationTime() {
        return lastCommunicationTime;
    }

    public Instant getLastNonZeroRuntime() {
        return lastNonZeroRuntime;
    }
}
