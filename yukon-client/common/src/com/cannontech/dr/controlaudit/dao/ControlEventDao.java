package com.cannontech.dr.controlaudit.dao;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.dr.controlaudit.ControlEventDeviceStatus;

public interface ControlEventDao {
    /**
     * Creates new control event associated with the specified load group
     */
    public void createNewEventMapping(int eventId, int groupId, Instant startTime, Instant stopTime);

    /**
     * Update device status (({@link ControlEventDeviceStatus}, deviceRecievedTime)) for which response is
     * received.
     */
    void updateDeviceControlEvent(int eventId, int deviceId,
            List<ControlEventDeviceStatus> skipUpdateForStatus, ControlEventDeviceStatus recievedMessageStatus,
            Instant deviceRecievedTime);

    /**
     * Insert event information for the device (in the specified load group) for which event as sent.
     */
    void insertDeviceControlEvent(int eventId, int loadGroupId);
    
    
}
