package com.cannontech.amr.deviceDataMonitor.service;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.core.dynamic.RichPointData;

public interface DeviceDataMonitorCalculationService {

    /**
     * Recalculates violation for monitor and point data. Adds device to violation group if violation was
     * found otherwise removes device from violation group.
     */
    void recalculateViolation(DeviceDataMonitor monitor, RichPointData pointData);
}
