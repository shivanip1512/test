package com.cannontech.amr.deviceDataMonitor.service;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
import com.cannontech.core.dynamic.PointValueHolder;

public interface DeviceDataMonitorCalculationService {
    /**
     * Returns a boolean indicating whether or not the passed in processor's LiteState.stateRawState
     * matches the passed in PointValueHolder's value
     */
    public boolean isPointValueMatch(DeviceDataMonitorProcessor processor, PointValueHolder pointValue);

    /**
     * Returns a boolean indicating whether or not a recalculation of violating devices should be
     * triggered if the passed in updatedMonitor and existingMonitor were to be saved. Used internally in the service
     * and in unit tests
     */
    public boolean shouldFindViolatingPaosBeforeSave(DeviceDataMonitor updatedMonitor, DeviceDataMonitor existingMonitor);

    /**
     * Returns a boolean indicating whether or not a monitor's violations device group
     * should be updated given an updatedMonitor and existingMonitor.
     * 
     * All this method is doing is performing an equality comparison of the monitor's violationsDeviceGroupPath
     * 
     * Used internally in the service and in unit tests
     */
    public boolean shouldUpdateViolationsGroupNameBeforeSave(DeviceDataMonitor updatedMonitor,
            DeviceDataMonitor existingMonitor);
}
