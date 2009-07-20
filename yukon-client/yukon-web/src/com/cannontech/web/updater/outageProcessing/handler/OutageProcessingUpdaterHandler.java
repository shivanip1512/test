package com.cannontech.web.updater.outageProcessing.handler;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.outageProcessing.OutageMonitorUpdaterTypeEnum;

public interface OutageProcessingUpdaterHandler {

	public String handle(int outageMonitorId, YukonUserContext userContext);
	
	public OutageMonitorUpdaterTypeEnum getUpdaterType();
}
