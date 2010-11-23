package com.cannontech.common.deviceReconfig.dao.impl;

import java.util.List;

import com.cannontech.common.deviceReconfig.dao.DeviceReconfigMonitorDao;
import com.cannontech.common.deviceReconfig.model.DeviceReconfigMonitor;
import com.cannontech.core.dao.NotFoundException;
import com.google.common.collect.Lists;

public class DeviceReconfigMonitorDaoImpl implements DeviceReconfigMonitorDao {

    @Override
    public boolean delete(int validationMonitorId) {
        return false;
    }

    @Override
    public List<DeviceReconfigMonitor> getAll() {
        List<DeviceReconfigMonitor> monitors = Lists.newArrayList();
        
        /* TEST */
        DeviceReconfigMonitor monitor1 = new DeviceReconfigMonitor();
        monitor1.setId(0);
        monitor1.setName("Unresponsive Feeder 1 10-29-2010");
        monitor1.setDeviceCount(349);
        DeviceReconfigMonitor monitor2 = new DeviceReconfigMonitor();
        monitor2.setId(1);
        monitor2.setName("Unresponsive Feeder 2 10-29-2010");
        monitor2.setDeviceCount(2588);
        DeviceReconfigMonitor monitor3 = new DeviceReconfigMonitor();
        monitor3.setId(1);
        monitor3.setName("Unresponsive Feeder 3 10-29-2010");
        monitor3.setDeviceCount(1448);
        
        monitors.add(monitor1);
        monitors.add(monitor2);
        monitors.add(monitor3);
        
        return monitors;
    }

    @Override
    public DeviceReconfigMonitor getById(int validationMonitorId) throws NotFoundException {
        return null;
    }

}