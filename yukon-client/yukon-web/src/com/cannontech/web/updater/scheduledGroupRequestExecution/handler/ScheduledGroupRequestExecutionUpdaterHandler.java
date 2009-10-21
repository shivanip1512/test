package com.cannontech.web.updater.scheduledGroupRequestExecution.handler;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.scheduledGroupRequestExecution.ScheduledGroupCommandRequestExecutionUpdaterTypeEnum;

public interface ScheduledGroupRequestExecutionUpdaterHandler {

	public String handle(int id, YukonUserContext userContext);
	
	public ScheduledGroupCommandRequestExecutionUpdaterTypeEnum getUpdaterType();
}
