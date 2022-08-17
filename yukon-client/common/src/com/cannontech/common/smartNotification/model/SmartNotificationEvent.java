package com.cannontech.common.smartNotification.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.Instant;

/**
 * Event for Smart Notifications. Each event indicates some discrete occurrence in the service sending it.
 * <br><br>
 * Each service generating these events should extend this class to create a custom event type for their specific
 * functionality. These sub-classes should contain any additional information necessary for notifications related to
 * that functionality. Any fields in the sub-class that need to be persisted should be returned in the parameters map.
 */
public class SmartNotificationEvent implements Serializable {
    private final static long serialVersionUID = 1L;
    private int eventId;
    private final Instant timestamp;
    private Instant groupProcessTime;
    private Instant immediateProcessTime;
    private Map<String, Object> parameters = new HashMap<>();
    
    public SmartNotificationEvent(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    public SmartNotificationEvent(int eventId, Instant timestamp) {
        this.timestamp = timestamp;
        this.eventId = eventId;
    }
        
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public void addParameters(String name, Object value) {
        parameters.put(name, value);
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    
    @Override
    public String toString() {
        return "SmartNotificationEvent [timestamp=" + timestamp.toDateTime().toString("MM-dd-yyyy HH:mm:ss") + ", parameters=" + getParameters() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + eventId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SmartNotificationEvent other = (SmartNotificationEvent) obj;
        if (eventId != other.eventId) {
            return false;
        }
        return true;
    }

    public Instant getGroupProcessTime() {
        return groupProcessTime;
    }

    public void setGroupProcessTime(Instant groupProcessTime) {
        this.groupProcessTime = groupProcessTime;
    }

    public Instant getImmediateProcessTime() {
        return immediateProcessTime;
    }

    public void setImmediateProcessTime(Instant immediateProcessTime) {
        this.immediateProcessTime = immediateProcessTime;
    }
}
