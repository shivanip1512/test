package com.cannontech.amr.deviceDataMonitor.service;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
import com.cannontech.core.dynamic.PointValueHolder;

public interface DeviceDataMonitorCalculationService {
    /**
     * Returns a boolean indicating whether or not the passed in processor's LiteState.stateRawState
     * matches the passed in PointValueHolder's value
     */
    public boolean isPointValueMatch(DeviceDataMonitorProcessor processor, PointValueHolder pointValue);

}
