package com.cannontech.web.updater.deviceDataMonitor.handler;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.deviceDataMonitor.DeviceDataMonitorUpdaterTypeEnum;

public interface DeviceDataUpdaterHandler {

	String handle(int monitorId, YukonUserContext userContext);
	
	DeviceDataMonitorUpdaterTypeEnum getUpdaterType();
	
	boolean isValueAvailableImmediately();
}
