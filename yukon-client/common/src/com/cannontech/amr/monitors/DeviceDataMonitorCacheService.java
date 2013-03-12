package com.cannontech.amr.monitors;

import java.util.List;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;

/**
 * A cache of all existing DeviceDataMonitor objects in the database. It is updated any time there are database
 * changes made (add/update/delete) to these objects
 */
public interface DeviceDataMonitorCacheService {
    List<DeviceDataMonitor> getAllMonitors();
    List<DeviceDataMonitor> getAllEnabledMonitors();
}
