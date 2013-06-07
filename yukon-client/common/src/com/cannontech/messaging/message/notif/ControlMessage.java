package com.cannontech.messaging.message.notif;

import java.util.Date;

import com.cannontech.messaging.message.BaseMessage;

public class ControlMessage extends BaseMessage {

    // types of control notifications
    public static final int STARTING_CONTROL_NOTIFICATION = 1;
    public static final int STARTING_NEVER_STOP_CONTROL_NOTIFICATION = 2;
    public static final int ADJUSTING_CONTROL_NOTIFICATION = 3;
    public static final int FINISHING_CONTROL_NOTIFICATION = 4;

    private int[] notifGroupIds = new int[0];
    private int notifType;
    private int programId;
    private Date startTime;
    private Date stopTime;

    public int[] getNotifGroupIds() {
        return notifGroupIds;
    }

    public void setNotifGroupIds(int[] notifGroupIds) {
        this.notifGroupIds = notifGroupIds;
    }

    public int getNotifType() {
        return notifType;
    }

    public void setNotifType(int notifType) {
        this.notifType = notifType;
    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }
}
