package com.cannontech.amr.monitors;

import java.util.List;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.common.validation.model.ValidationMonitor;

/**
 * A cache of all existing monitors. It is updated any time there are database
 * changes made (add/update/delete) to these objects
 */
public interface MonitorCacheService {

    DeviceDataMonitor getDeviceMonitor(int id);

    List<DeviceDataMonitor> getDeviceDataMonitors();

    List<DeviceDataMonitor> getEnabledDeviceDataMonitors();

    List<OutageMonitor> getOutageMonitors();

    OutageMonitor getOutageMonitor(int id);

    List<TamperFlagMonitor> getTamperFlagMonitors();

    TamperFlagMonitor getTamperFlagMonitor(int id);

    List<StatusPointMonitor> getStatusPointMonitors();

    StatusPointMonitor getStatusPointMonitor(int id);

    List<PorterResponseMonitor> getPorterResponseMonitors();

    PorterResponseMonitor getPorterResponseMonitor(int id);

    ValidationMonitor getValidationMonitor(int id);

    List<ValidationMonitor> getValidationMonitors();

    List<PorterResponseMonitor> getEnabledPorterResponseMonitors();
}
