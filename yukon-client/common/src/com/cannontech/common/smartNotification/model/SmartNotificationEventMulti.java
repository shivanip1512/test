package com.cannontech.common.smartNotification.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.appendSuper(super.toString());
        tsb.append("type", type);
        tsb.append("events", events);
        return tsb.toString();
    }
}
