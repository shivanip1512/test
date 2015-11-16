package com.cannontech.web.updater.deviceReconfig.handler;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.deviceReconfig.DeviceReconfigMonitorUpdaterType;

public interface DeviceReconfigUpdaterHandler {

    public String handle(int monitorId, YukonUserContext userContext);
    
    public DeviceReconfigMonitorUpdaterType getUpdaterType();
    
}