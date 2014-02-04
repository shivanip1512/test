package com.cannontech.dr.model;

import org.joda.time.Instant;

public final class PerformanceVerificationEventMessageStats {

    private final PerformanceVerificationEventMessage eventMessage;
    private final PerformanceVerificationEventStats eventStats;

    public PerformanceVerificationEventMessageStats(long messageId, Instant timeMessageSent, int numSuccesses, 
                                                    int numFailures, int numUnknowns) {
        eventMessage = new PerformanceVerificationEventMessage(messageId, timeMessageSent);
        eventStats = new PerformanceVerificationEventStats(numSuccesses, numFailures, numUnknowns);
    }

    public long getMessageId() {
        return eventMessage.getMessageId();
    }

    public Instant getTimeMessageSent() {
        return eventMessage.getTimeMessageSent();
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
