package com.cannontech.common.smartNotification.model;

import java.io.Serializable;
import java.util.List;

/**
 * A simple container for multiple smart notification events, for use by event producers that tend to produce multiple
 * events all at once.
 */
public class SmartNotificationEventMulti implements Serializable {
    private final static long serialVersionUID = 1L;
    private final List<SmartNotificationEvent> events;
    
    public SmartNotificationEventMulti(List<SmartNotificationEvent> events) {
        this.events = events;
    }
    
    public List<SmartNotificationEvent> getEvents() {
        return events;
    }
}
