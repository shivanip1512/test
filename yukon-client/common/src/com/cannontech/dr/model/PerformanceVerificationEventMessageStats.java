package com.cannontech.dr.model;

import org.joda.time.Instant;

public final class PerformanceVerificationEventMessageStats {

    private final int messageId;
    private final Instant messageSent;
    private final PerformanceVerificationEventStats eventStats;

    public PerformanceVerificationEventMessageStats(int messageId, Instant messageSent, int success, 
                                                    int failed, int unknown) {
        this.messageId = messageId;
        this.messageSent = messageSent;
        eventStats = new PerformanceVerificationEventStats(success, failed, unknown);
    }

    public int getMessageId() {
        return messageId;
    }

    public Instant getMessageSent() {
        return messageSent;
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
