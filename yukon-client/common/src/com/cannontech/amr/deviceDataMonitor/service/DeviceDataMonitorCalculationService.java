package com.cannontech.amr.deviceDataMonitor.service;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.RichPointData;

public interface DeviceDataMonitorCalculationService {

    /**
     * Recalculates violation for device. Adds device to violation group if violation was
     * found otherwise removes device from violation group.
     */

    void updateViolationsGroupBasedOnNewPointData(DeviceDataMonitor monitor, RichPointData richPointData, BuiltInAttribute attribute);
}
