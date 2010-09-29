package com.cannontech.web.updater.statusPointMonitoring.handler;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.statusPointMonitoring.StatusPointMonitorUpdaterTypeEnum;

public interface StatusPointProcessingUpdaterHandler {

	public String handle(int statusPointMonitorId, YukonUserContext userContext);
	
	public StatusPointMonitorUpdaterTypeEnum getUpdaterType();
}
