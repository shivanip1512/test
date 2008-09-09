package com.cannontech.message.porter.message;

import java.util.Date;

import com.cannontech.message.util.Message;

public class RequestCancel extends Message {

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
