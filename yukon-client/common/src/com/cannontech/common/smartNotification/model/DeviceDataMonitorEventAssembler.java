package com.cannontech.common.smartNotification.model;

import java.util.Map;

import org.joda.time.Instant;

import com.google.common.collect.ImmutableMap;


public class DeviceDataMonitorEventAssembler {
    public static final String PAO_ID = "paoId";
    public static final String MONITOR_ID = "monitorId";
    public static final String STATE = "MonitorState";

    public static SmartNotificationEvent assemble(Instant now, int monitorId, MonitorState state, int paoId) {
        SmartNotificationEvent event = new SmartNotificationEvent(now);
        event.setParameters(ImmutableMap.of(
            PAO_ID, paoId, 
            MONITOR_ID, monitorId, 
            STATE, state));
        return event;
    }
    
    public static enum MonitorState {
        IN_VIOLATION,
        OUT_OF_VIOLATION,
        ;
    }
    
    public static int getMonitorId(Map<String, Object> parameters){
        int monitorId = Integer.parseInt(parameters.get(MONITOR_ID).toString());
        return monitorId;
    }
    
    public static int getPaoId(Map<String, Object> parameters){
        int paoId = Integer.parseInt(parameters.get(PAO_ID).toString());
        return paoId;
    }
    
    public static MonitorState getState(Map<String, Object> parameters){
        MonitorState state = MonitorState.valueOf(parameters.get(STATE).toString());
        return state;
    }
}
