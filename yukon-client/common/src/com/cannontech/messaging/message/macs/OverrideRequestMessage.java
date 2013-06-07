package com.cannontech.messaging.message.macs;

import java.util.Date;

import com.cannontech.messaging.message.BaseMessage;

public class OverrideRequestMessage extends BaseMessage {

    // Correspond to the C++ enumeration
    public static final int OVERRIDE_START = 0;
    public static final int OVERRIDE_START_NOW = 1;
    public static final int OVERRIDE_STOP = 2;
    public static final int OVERRIDE_STOP_NOW = 3;
    public static final int OVERRIDE_ENABLE = 4;
    public static final int OVERRIDE_DISABLE = 5;

    // action is either one of the above values
    private int action = -1;
    private long schedId = -1;
    private Date start = new java.sql.Date(0);
    private Date stop = new java.sql.Date(0);

    public int getAction() {
        return action;
    }

    public long getSchedId() {
        return schedId;
    }

    public java.util.Date getStart() {
        return start;
    }

    public java.util.Date getStop() {
        return stop;
    }

    public void setAction(int newAction) {
        action = newAction;
    }

    public void setSchedId(long newSchedId) {
        schedId = newSchedId;
    }

    public void setStart(java.util.Date newStart) {
        start = newStart;
    }

    public void setStop(java.util.Date newStop) {
        stop = newStop;
    }
}
