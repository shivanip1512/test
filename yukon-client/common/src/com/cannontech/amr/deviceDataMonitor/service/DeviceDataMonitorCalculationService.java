package com.cannontech.amr.deviceDataMonitor.service;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;

public interface DeviceDataMonitorCalculationService {

    /**
     * Recalculates violation for device. Adds device to violation group if violation was
     * found otherwise removes device from violation group.
     */

    void recalculateViolation(DeviceDataMonitor monitor, int deviceId);
}
