package com.cannontech.messaging.message.macs;

import com.cannontech.messaging.message.BaseMessage;

public class DeleteScheduleMessage extends BaseMessage {

    private long id;

    public long getScheduleId() {
        return id;
    }

    public void setScheduleId(long newId) {
        id = newId;
    }
}
