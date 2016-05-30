package com.cannontech.dr.model;

import org.joda.time.Instant;

public final class PerformanceVerificationEventMessage {

    private final long messageId;
    private final Instant timeMessageSent;
    private final Boolean isArchived;

    public PerformanceVerificationEventMessage(long messageId, Instant timeMessageSent, Boolean isArchived) {
        this.messageId = messageId;
        this.timeMessageSent = timeMessageSent;
        this.isArchived = isArchived;
    }

    public long getMessageId() {
        return messageId;
    }

    public Instant getTimeMessageSent() {
        return timeMessageSent;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }
}
