package com.cannontech.common.deviceReconfig.dao;

import java.util.List;

import com.cannontech.common.deviceReconfig.model.DeviceReconfigMonitor;
import com.cannontech.core.dao.NotFoundException;

public interface DeviceReconfigMonitorDao{

    public DeviceReconfigMonitor getById(int validationMonitorId) throws NotFoundException;

    public List<DeviceReconfigMonitor> getAll();

    public boolean delete(int validationMonitorId);

}