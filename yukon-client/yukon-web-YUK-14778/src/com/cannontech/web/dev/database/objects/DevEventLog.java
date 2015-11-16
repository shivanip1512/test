package com.cannontech.web.dev.database.objects;

import java.util.Map;
import java.util.TreeMap;

import com.cannontech.common.events.model.EventSource;
import com.cannontech.web.dev.database.service.impl.DevEventLogCreationService;

public class DevEventLog {

    private static Map<DevEventLogCreationService.LogType,Boolean> eventLogTypes = new TreeMap<>();

    static {
        for(DevEventLogCreationService.LogType logType : DevEventLogCreationService.LogType.values()) {
            eventLogTypes.put(logType, true);
        }
    }
        
    private String username = "yukon";
    private String indicatorString = "fake_";
    
    private int iterations = 1;
    
    private EventSource eventSource = EventSource.OPERATOR;
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIndicatorString() {
        return indicatorString;
    }

    public void setIndicatorString(String indicatorString) {
        this.indicatorString = indicatorString;
    }

    public EventSource getEventSource() {
        return eventSource;
    }

    public void setEventSource(EventSource eventSource) {
        this.eventSource = eventSource;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getTotal() {
        int total = 0;

        for(DevEventLogCreationService.LogType logType : DevEventLogCreationService.LogType.values()) {
            total += eventLogTypes.get(logType) ? logType.getNumberOfMethodsTested() : 0;
        }

        return total * iterations;
    }
    
    public Map<DevEventLogCreationService.LogType, Boolean> getEventLogTypes() {
        return eventLogTypes;
    }
}
