package com.cannontech.dr.model;

import org.joda.time.Instant;

public final class PerformanceVerificationEventMessage {

    private final long messageId;
    private final Instant timeMessageSent;

    public PerformanceVerificationEventMessage(long messageId, Instant timeMessageSent) {
        this.messageId = messageId;
        this.timeMessageSent = timeMessageSent;
    }

    public long getMessageId() {
        return messageId;
    }

    public Instant getTimeMessageSent() {
        return timeMessageSent;
    }
}
