package com.cannontech.common.smartNotification.model;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.joda.time.Instant;


public class MeterDrEventAssembler {

    public static final String PROGRAM_NAME = "programName";
    public static SmartNotificationEvent assemble(ConcurrentMap<String, Integer> statistics, String programName) {
        SmartNotificationEvent event = new SmartNotificationEvent(Instant.now());
        event.getParameters().putAll(statistics);
        event.getParameters().put(PROGRAM_NAME, programName);
        return event;
    }
    
    public static String getProgramName(Map<String, Object> parameters) {
        return parameters.get(PROGRAM_NAME).toString();
    }
}
