package com.cannontech.messaging.message.macs;

import com.cannontech.messaging.message.BaseMessage;

public class RetrieveScheduleMessage extends BaseMessage {

    public static long ALL_SCHEDULES = 0;
    private long id;

    public long getScheduleId() {
        return id;
    }

    public void setScheduleId(long newId) {
        id = newId;
    }
}
