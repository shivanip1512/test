package com.cannontech.common.smartNotification.model;

import java.io.Serializable;
import java.util.Map;

import org.joda.time.Instant;

/**
 * Event for Smart Notifications. Each event indicates some discrete occurrence in the service sending it.
 * <br><br>
 * Each service generating these events should extend this class to create a custom event type for their specific
 * functionality. These sub-classes should contain any additional information necessary for notifications related to
 * that functionality. Any fields in the sub-class that need to be persisted should be returned in the parameters map.
 */
public abstract class SmartNotificationEvent implements Serializable {
    private final static long serialVersionUID = 1L;
    private final SmartNotificationEventType type;
    private final Instant timestamp;
    
    public SmartNotificationEvent(SmartNotificationEventType type) {
        this(type, Instant.now());
    }
    
    public SmartNotificationEvent(SmartNotificationEventType type, Instant timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }
    
    public SmartNotificationEventType getType() {
        return type;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    /**
     * @return A map of parameters that are specific to the event sub-type.
     */
    public abstract Map<String,Object> getParameters();

    @Override
    public String toString() {
        return "SmartNotificationEvent [type=" + type + ", timestamp=" + timestamp + ", parameters=" + getParameters() + "]";
    }
}
