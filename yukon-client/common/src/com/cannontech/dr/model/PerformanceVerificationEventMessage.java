package com.cannontech.dr.model;

import org.joda.time.Instant;

public final class PerformanceVerificationEventMessage {

    private final long messageId;
    private final Instant timeMessageSent;
    private final boolean archived;

    public PerformanceVerificationEventMessage(long messageId, Instant timeMessageSent, boolean archived) {
        this.messageId = messageId;
        this.timeMessageSent = timeMessageSent;
        this.archived = archived;
    }

    public long getMessageId() {
        return messageId;
    }

    public Instant getTimeMessageSent() {
        return timeMessageSent;
    }

    public boolean isArchived() {
        return archived;
    }


}
