package com.cannontech.common.smartNotification.model;

import org.joda.time.Instant;

import com.google.common.collect.ImmutableMap;

/**
 * A Smart Notification event sent when a device is added or removed from the violation group by a Device Data Monitor.
 */
public class DeviceDataMonitorEventAssembler {
    public static final String PAO_ID = "paoId";
    public static final String MONITOR_ID = "MonitorId";
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
}
