package com.cannontech.dr.model;

import org.joda.time.Instant;

public final class PerformanceVerificationEventMessageStats {

    private final PerformanceVerificationEventMessage eventMessage;
    private final PerformanceVerificationEventStats eventStats;

    public PerformanceVerificationEventMessageStats(int messageId, Instant messageSent, int success, 
                                                    int failed, int unknown) {
        eventMessage = new PerformanceVerificationEventMessage(messageId, messageSent);
        eventStats = new PerformanceVerificationEventStats(success, failed, unknown);
    }

    public int getMessageId() {
        return eventMessage.getMessageId();
    }

    public Instant getMessageSent() {
        return eventMessage.getMessageSent();
    }

    public int getSuccess() {
        return eventStats.getSuccess();
    }

    public int getFailed() {
        return eventStats.getFailed();
    }

    public int getUnknown() {
        return eventStats.getUnknown();
    }

    public double getPercentSuccess() {
        return eventStats.getPercentSuccess();
    }
}
