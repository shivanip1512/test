package com.cannontech.common.smartNotification.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple container for multiple smart notification events, for use by event producers that tend to produce multiple
 * events all at once.
 */
public class SmartNotificationEventMulti implements Serializable {
    private final static long serialVersionUID = 1L;
    public final SmartNotificationEventType type;
    private final List<SmartNotificationEvent> events;
    
    public SmartNotificationEventMulti(SmartNotificationEventType type, List<SmartNotificationEvent> events) {
        this.events = new ArrayList<>(events);
        this.type = type;
    }
    
    public List<SmartNotificationEvent> getEvents() {
        return events;
    }
    
    public SmartNotificationEventType getEventType(){
        return type;
    }
    
    @Override
    public String toString() {
        return "SmartNotificationMulti [type=" + type + "] "+ events.stream().map(e -> e.toString()).collect(Collectors.toList());
    }
}
