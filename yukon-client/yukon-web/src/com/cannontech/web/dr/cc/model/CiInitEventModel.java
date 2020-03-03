package com.cannontech.web.dr.cc.model;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.cannontech.cc.model.CiEventType;

/**
 * Model object used by pages that initialize a curtailment event.
 */
public class CiInitEventModel {
    private int programId;
    private CiEventType eventType;
    private DateTime notificationTime;
    private DateTime startTime;
    /** Event duration, in minutes */
    private int duration;
    private String message;
    private int numberOfWindows;
    private List<Integer> selectedGroupIds;
    private List<Integer> selectedCustomerIds;
    private List<BigDecimal> windowPrices;
    private Integer initialEventId;
    private Integer adjustEventId;

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
    
    /**
     * @return The event duration, in minutes.
     */
    public int getDuration() {
        return duration;
    }
    
    /**
     * @param duration The event duration, in minutes.
     */
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

    public List<BigDecimal> getWindowPrices() {
        return windowPrices;
    }

    public void setWindowPrices(List<BigDecimal> windowPrices) {
        this.windowPrices = windowPrices;
    }
    
    public Integer getInitialEventId() {
        return initialEventId;
    }

    public void setInitialEventId(Integer initialEventId) {
        this.initialEventId = initialEventId;
    }
    
    public boolean isEventExtension() {
        return initialEventId != null;
    }
    
    public Integer getAdjustEventId() {
        return adjustEventId;
    }
    
    public void setAdjustEventId(int eventId) {
        adjustEventId = eventId;
    }
    
    public boolean isEventAdjustment() {
        return adjustEventId != null;
    }
}
