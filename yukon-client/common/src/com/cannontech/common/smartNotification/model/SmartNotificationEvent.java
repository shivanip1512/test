package com.cannontech.common.smartNotification.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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
    private Instant timestamp;
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
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.appendSuper(super.toString());
        tsb.append("eventId", eventId);
        tsb.append("timestamp", timestamp.toDateTime().toString("MM-dd-yyyy HH:mm:ss"));
        if (groupProcessTime != null) {
            tsb.append("groupProcessTime", groupProcessTime.toDateTime().toString("MM-dd-yyyy HH:mm:ss"));
        }
        if (immediateProcessTime != null) {
            tsb.append("immediateProcessTime", immediateProcessTime.toDateTime().toString("MM-dd-yyyy HH:mm:ss"));
        }
        tsb.append("parameters", parameters);
        return tsb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + eventId;
        result = prime * result + ((groupProcessTime == null) ? 0 : groupProcessTime.hashCode());
        result = prime * result + ((immediateProcessTime == null) ? 0 : immediateProcessTime.hashCode());
        result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SmartNotificationEvent other = (SmartNotificationEvent) obj;
        if (eventId != other.eventId)
            return false;
        if (groupProcessTime == null) {
            if (other.groupProcessTime != null)
                return false;
        } else if (!groupProcessTime.equals(other.groupProcessTime))
            return false;
        if (immediateProcessTime == null) {
            if (other.immediateProcessTime != null)
                return false;
        } else if (!immediateProcessTime.equals(other.immediateProcessTime))
            return false;
        if (parameters == null) {
            if (other.parameters != null)
                return false;
        } else if (!parameters.equals(other.parameters))
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        return true;
    }

}
