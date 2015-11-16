package com.cannontech.web.updater.tamperFlagProcessing.handler;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.tamperFlagProcessing.TamperFlagMonitorUpdaterTypeEnum;

public interface TamperFlagProcessingUpdaterHandler {

	public String handle(int tamperFlagMonitorId, YukonUserContext userContext);
	
	public TamperFlagMonitorUpdaterTypeEnum getUpdaterType();
}
