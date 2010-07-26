package com.cannontech.common.events.model;

import java.util.Map;

import com.google.common.collect.Maps;

public class MappedEventLog {
    private EventLog eventLog;
    private Map<EventParameter, Object> parameterMap = Maps.newLinkedHashMap();

    public EventLog getEventLog() {
        return eventLog;
    }
    public void setEventLog(EventLog eventLog) {
        this.eventLog = eventLog;
    }
    
    public Map<EventParameter, Object> getParameterMap() {
        return parameterMap;
    }
}
