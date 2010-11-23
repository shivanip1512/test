package com.cannontech.web.updater.deviceReconfig.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.deviceReconfig.dao.DeviceReconfigMonitorDao;
import com.cannontech.common.deviceReconfig.model.DeviceReconfigMonitor;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.deviceReconfig.DeviceReconfigMonitorUpdaterType;

public class DeviceReconfigProgressUpdaterHandler implements DeviceReconfigUpdaterHandler {

    private DeviceReconfigMonitorDao deviceReconfigMonitorDao;

    @Override
    public String handle(int monitorId, YukonUserContext userContext) {

        String progress = "N/A";
        
        try {
            
            DeviceReconfigMonitor deviceReconfigMonitor = deviceReconfigMonitorDao.getById(monitorId);
            
            
        } catch (NotFoundException e) {
            // TODO
        }
        
        /* TEST */
        if(monitorId == 0) {
            progress = "In Progress 47%";
        } else if (monitorId == 1) {
            progress = "In Progress 16%";
        } else if (monitorId == 2) {
            progress = "Complete";
        }
        
        return progress;
    }

    @Override
    public DeviceReconfigMonitorUpdaterType getUpdaterType() {
        return DeviceReconfigMonitorUpdaterType.PROGRESS;
    }
    
    @Autowired
    public void setDeviceReconfigMonitorDao(DeviceReconfigMonitorDao deviceReconfigMonitorDao) {
        this.deviceReconfigMonitorDao = deviceReconfigMonitorDao;
    }
    
}