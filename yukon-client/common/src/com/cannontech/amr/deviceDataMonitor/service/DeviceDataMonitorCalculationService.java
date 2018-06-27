package com.cannontech.amr.deviceDataMonitor.service;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dynamic.PointValueQualityHolder;

public interface DeviceDataMonitorCalculationService {

    /**
     * Recalculates violation for device. Adds device to violation group if violation was
     * found otherwise removes device from violation group.
     * @return false - if violations were not recalculated because the attributes were not supported by the device
     */

    boolean recalculateViolation(DeviceDataMonitor monitor, SimpleDevice device, PointValueQualityHolder pointValue);
}
