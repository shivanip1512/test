package com.cannontech.messaging.message.notif;

import java.util.Arrays;
import java.util.Date;

import com.cannontech.messaging.message.BaseMessage;

public class ProgramActionMessage extends BaseMessage {

    private int programId;
    private String eventDisplayName;
    private String action;
    private Date startTime;
    private Date stopTime;
    private Date notificationTime;
    private int[] customerIds;

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public String getEventDisplayName() {
        return eventDisplayName;
    }

    public void setEventDisplayName(String eventDisplayName) {
        this.eventDisplayName = eventDisplayName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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

    public Date getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(Date notificationTime) {
        this.notificationTime = notificationTime;
    }

    public int[] getCustomerIds() {
        return customerIds;
    }

    public void setCustomerIds(int[] customerIds) {
        this.customerIds = customerIds;
    }

    @Override
    public String toString() {
        return String
            .format("ProgramActionMsg [programId=%s, eventDisplayName=%s, action=%s, startTime=%s, stopTime=%s, notificationTime=%s, customerIds=%s, parent=%s]",
                    programId, eventDisplayName, action, startTime, stopTime, notificationTime,
                    Arrays.toString(customerIds), super.toString());
    }
}
