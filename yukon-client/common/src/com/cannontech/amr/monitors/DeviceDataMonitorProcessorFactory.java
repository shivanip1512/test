package com.cannontech.amr.monitors;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.core.dynamic.RichPointData;

public interface DeviceDataMonitorProcessorFactory {
    void handlePointDataReceived(final DeviceDataMonitor monitor, RichPointData richPointData);
}
