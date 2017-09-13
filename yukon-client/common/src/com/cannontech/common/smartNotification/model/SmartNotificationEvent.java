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
    private final Instant timestamp;
    private Map<String, Object> parameters = new HashMap<>();
    
    public SmartNotificationEvent(Instant timestamp) {
        this.timestamp = timestamp;
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

    @Override
    public String toString() {
        return "SmartNotificationEvent [timestamp=" + timestamp + ", parameters=" + getParameters() + "]";
    }
    
    public Object getParameter(String key){
        return parameters.get(key);
    }
}
