package com.cannontech.messaging.message.porter;

import java.util.Date;

import com.cannontech.messaging.message.BaseMessage;

public class QueueDataMessage extends BaseMessage {

    private long queueId = 0;
    private long requestId = 0;
    private long rate = 0;
    private long queueCount = 0;
    private long requestIdCount = 0;
    private long userMessageId = 0;
    private Date time;

    public long getQueueCount() {
        return queueCount;
    }

    public void setQueueCount(long queueCount) {
        this.queueCount = queueCount;
    }

    public long getQueueId() {
        return queueId;
    }

    public void setQueueId(long queueId) {
        this.queueId = queueId;
    }

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public long getRequestIdCount() {
        return requestIdCount;
    }

    public void setRequestIdCount(long requestIdCount) {
        this.requestIdCount = requestIdCount;
    }

    public long getUserMessageId() {
        return userMessageId;
    }

    public void setUserMessageId(long userMessageId) {
        this.userMessageId = userMessageId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
