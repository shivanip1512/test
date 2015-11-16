package com.cannontech.web.updater.scheduledGroupRequestExecution.handler;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionBundle;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.scheduledGroupRequestExecution.ScheduledGroupCommandRequestExecutionUpdaterTypeEnum;

public interface ScheduledGroupRequestExecutionUpdaterHandler {

	public String handle(ScheduledGroupRequestExecutionBundle execution, YukonUserContext userContext);
	
	public ScheduledGroupCommandRequestExecutionUpdaterTypeEnum getUpdaterType();
}
