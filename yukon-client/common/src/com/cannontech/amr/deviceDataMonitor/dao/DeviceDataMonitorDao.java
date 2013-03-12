package com.cannontech.amr.deviceDataMonitor.dao;

import java.util.List;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.core.dao.NotFoundException;

public interface DeviceDataMonitorDao {

    /**
     * Saves our Monitor to the DB.
     * 
     * **IMPORTANT** - this method should NEVER be called from anywhere but the
     * DeviceDataMonitorService.save method. The reason for this is b/c the service performs
     * a couple other *necessary* steps (calculate violations) before the actual db save
     */
    public void save(DeviceDataMonitor monitor);

    public DeviceDataMonitor getMonitorById(Integer monitorId) throws NotFoundException;

    public List<DeviceDataMonitor> getAllMonitors();

    public boolean deleteMonitor(int monitorId);
}
