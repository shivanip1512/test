package com.cannontech.common.smartNotification.model;

import java.util.Map;

import org.joda.time.Instant;

import com.google.common.collect.ImmutableMap;

/**
 * A Smart Notification event sent when a device is added or removed from the violation group by a Device Data Monitor.
 */
public class DeviceDataMonitorSmartNotificationEvent extends SmartNotificationEvent {
    private final MonitorState monitorState;
    private final int deviceDataMonitorId;
    private final int paoId;
    
    public DeviceDataMonitorSmartNotificationEvent(Instant timestamp, int deviceDataMonitorId, 
                                                   MonitorState monitorState, int paoId) {
        super(SmartNotificationEventType.DEVICE_DATA_MONITOR, timestamp);
        this.deviceDataMonitorId = deviceDataMonitorId;
        this.monitorState = monitorState;
        this.paoId = paoId;
    }
    
    @Override
    public Map<String, Object> getParameters() {
        return ImmutableMap.of(
            "PaoId", paoId,
            "MonitorId", deviceDataMonitorId,
            "MonitorState", monitorState
        );
    }
    
    public MonitorState getMonitorState() {
        return monitorState;
    }

    public int getDeviceDataMonitorId() {
        return deviceDataMonitorId;
    }

    public int getPaoId() {
        return paoId;
    }

    public static enum MonitorState {
        IN_VIOLATION,
        OUT_OF_VIOLATION,
        ;
    }
}
