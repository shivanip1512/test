package com.cannontech.dr.model;

import org.joda.time.Instant;

public final class PerformanceVerificationEventMessage {

    private final int messageId;
    private final Instant messageSent;

    public PerformanceVerificationEventMessage(int messageId, Instant messageSent) {
        this.messageId = messageId;
        this.messageSent = messageSent;
    }

    public int getMessageId() {
        return messageId;
    }

    public Instant getMessageSent() {
        return messageSent;
    }
}
