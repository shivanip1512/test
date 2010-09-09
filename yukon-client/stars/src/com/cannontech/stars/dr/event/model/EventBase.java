package com.cannontech.stars.dr.event.model;

import org.joda.time.Instant;

public class EventBase {
    private int eventId;
    private int userId;
    private int systemCategoryId;
    private int actionId;
    private Instant eventTimestamp;
    
    public int getEventId() {
        return eventId;
    }
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getSystemCategoryId() {
        return systemCategoryId;
    }
    public void setSystemCategoryId(int systemCategoryId) {
        this.systemCategoryId = systemCategoryId;
    }
    
    public int getActionId() {
        return actionId;
    }
    public void setActionId(int actionId) {
        this.actionId = actionId;
    }
    
    public Instant getEventTimestamp() {
        return eventTimestamp;
    }
    public void setEventTimestamp(Instant eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }
    
}