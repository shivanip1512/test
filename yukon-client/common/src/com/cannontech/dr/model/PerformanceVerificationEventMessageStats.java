package com.cannontech.dr.model;

import org.joda.time.Instant;

public final class PerformanceVerificationEventMessageStats {

    private final PerformanceVerificationEventMessage eventMessage;
    private final PerformanceVerificationEventStats eventStats;

    public PerformanceVerificationEventMessageStats(long messageId, 
                                                    Instant timeMessageSent, 
                                                    Boolean isArchived,
                                                    int numSuccesses, 
                                                    int numFailures, 
                                                    int numUnknowns) {
        
        eventMessage = new PerformanceVerificationEventMessage(messageId, timeMessageSent,isArchived);
        eventStats = new PerformanceVerificationEventStats(numSuccesses, numFailures, numUnknowns);
    }
    
    public PerformanceVerificationEventStats getEventStats() {
        return eventStats;
    }

    public long getMessageId() {
        return eventMessage.getMessageId();
    }

    public Instant getTimeMessageSent() {
        return eventMessage.getTimeMessageSent();
    }
    
    public PerformanceVerificationEventMessage getEventMessage() {
        return eventMessage;
    }

    public int getNumSuccesses() {
        return eventStats.getNumSuccesses();
    }

    public int getNumFailures() {
        return eventStats.getNumFailures();
    }

    public int getNumUnknowns() {
        return eventStats.getNumUnknowns();
    }

    public double getPercentSuccess() {
        return eventStats.getPercentSuccess();
    }
    
}