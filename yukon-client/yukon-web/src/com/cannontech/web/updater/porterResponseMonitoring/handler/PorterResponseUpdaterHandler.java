package com.cannontech.web.updater.porterResponseMonitoring.handler;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.porterResponseMonitoring.PorterResponseMonitorUpdaterTypeEnum;

public interface PorterResponseUpdaterHandler {
    
    public String handle(int porterResponseMonitorId, YukonUserContext userContext);
    
    public PorterResponseMonitorUpdaterTypeEnum getUpdaterType();
}
