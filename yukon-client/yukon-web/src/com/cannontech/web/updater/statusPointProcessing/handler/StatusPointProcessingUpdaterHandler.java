package com.cannontech.web.updater.statusPointProcessing.handler;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.statusPointProcessing.StatusPointMonitorUpdaterTypeEnum;

public interface StatusPointProcessingUpdaterHandler {

	public String handle(int statusPointMonitorId, YukonUserContext userContext);
	
	public StatusPointMonitorUpdaterTypeEnum getUpdaterType();
}
