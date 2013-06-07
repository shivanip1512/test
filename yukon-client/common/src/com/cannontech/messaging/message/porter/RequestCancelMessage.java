package com.cannontech.messaging.message.porter;

import java.util.Date;

import com.cannontech.messaging.message.BaseMessage;

public class RequestCancelMessage extends BaseMessage {

    private long requestId;
    private long requestIdCount;
    private long userMessageId;
    private Date time;

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
