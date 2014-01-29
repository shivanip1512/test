package com.cannontech.dr;

import org.joda.time.DateTime;

public final class PerformanceVerificationEvent {
    private final int broadcastEventId;
    private final DateTime eventCreated;
    
    public PerformanceVerificationEvent(int broadcastEventId, DateTime eventCreated) {
        this.broadcastEventId = broadcastEventId;
        this.eventCreated = eventCreated;
    }
    
    public int getBroadcastEventId() {
        return broadcastEventId;
    }
    
    public DateTime getEventCreated() {
        return eventCreated;
    }
}
