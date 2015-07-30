package com.cannontech.web.dr.cc.model;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Model object used by pages that initialize a curtailment event.
 */
public class CiInitEventModel {
    private int programId;
    private CiEventType eventType;
    private DateTime notificationTime;
    private DateTime startTime;
    private int duration = 240;
    private String message;
    private int numberOfWindows = 4;
    private List<Integer> selectedGroupIds;
    private List<Integer> selectedCustomerIds;
    
    public int getProgramId() {
        return programId;
    }
    
    public void setProgramId(int programId) {
        this.programId = programId;
    }
    
    public CiEventType getEventType() {
        return eventType;
    }
    
    public void setEventType(CiEventType eventType) {
        this.eventType = eventType;
    }

    public DateTime getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(DateTime notificationTime) {
        this.notificationTime = notificationTime;
    }

    public DateTime getStartTime() {
        return startTime;
    }
    
    public DateTime getStopTime() {
        return startTime.plus(Duration.standardHours(duration));
    }
    
    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getNumberOfWindows() {
        return numberOfWindows;
    }

    public void setNumberOfWindows(int numberOfWindows) {
        this.numberOfWindows = numberOfWindows;
    }

    public List<Integer> getSelectedGroupIds() {
        return selectedGroupIds;
    }

    public void setSelectedGroupIds(List<Integer> selectedGroupIds) {
        this.selectedGroupIds = selectedGroupIds;
    }
    
    public List<Integer> getSelectedCustomerIds() {
        return selectedCustomerIds;
    }

    public void setSelectedCustomerIds(List<Integer> selectedCustomerIds) {
        this.selectedCustomerIds = selectedCustomerIds;
    }
}
