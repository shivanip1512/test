package com.cannontech.dr.model;

import org.joda.time.Instant;

public class MutablePerformanceVerificationEventStats {

    private int messageId;
    private Instant messageSent;
    private int success;
    private int failed;
    private int unknown;

    public void addStatus(PerformanceVerificationMessageStatus status) {
        switch(status) {
            case SUCCESS : success++; break;
            case UNSUCCESS : failed++; break;
            case UNKNOWN : unknown++; break;
        }
    }

    public void setMessageSent(Instant messageSent) {
        this.messageSent = messageSent;
    }
    
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
    
    public int getMessageId() {
        return messageId;
    }

    public Instant getMessageSent() {
        return messageSent;
    }

    public int getSuccess() {
        return success;
    }

    public int getFailed() {
        return failed;
    }

    public int getUnknown() {
        return unknown;
    }
    
    public PerformanceVerificationEventStats getImmutableStats() {
        return new PerformanceVerificationEventStats(success, failed, unknown);
    }
    
    public PerformanceVerificationEventMessageStats getImmutableMessageStats() {
        return new PerformanceVerificationEventMessageStats(messageId, messageSent, success, failed, unknown);
    }
}
