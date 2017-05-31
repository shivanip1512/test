package com.cannontech.dr.controlaudit.service;

import org.joda.time.Instant;

import com.cannontech.dr.controlaudit.ControlEventDeviceStatus;
import com.cannontech.dr.honeywellWifi.azure.event.EventPhase;

public interface ControlEventService {
    /**
     * Update device status (({@link ControlEventDeviceStatus}, deviceRecievedTime)) for which response is
     * received.
     */
    public void updateDeviceControlEvent(int eventId, int deviceId, EventPhase eventPhase,
            Instant deviceRecievedTime);

    /**
     * Creates new control event associated with the specified load group
     */
    public void createDeviceControlEvent(int eventId, int groupId, Instant startTime, Instant stopTime);
}
