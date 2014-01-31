package com.cannontech.dr.model;

import org.joda.time.Instant;

public final class PerformanceVerificationEventMessage {

    private final long messageId;
    private final Instant messageSent;

    public PerformanceVerificationEventMessage(long messageId, Instant messageSent) {
        this.messageId = messageId;
        this.messageSent = messageSent;
    }

    public long getMessageId() {
        return messageId;
    }

    public Instant getMessageSent() {
        return messageSent;
    }
}
