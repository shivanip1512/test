package com.cannontech.web.updater.scheduledGroupRequestExecution.handler;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionBundle;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.scheduledGroupRequestExecution.ScheduledGroupCommandRequestExecutionUpdaterTypeEnum;

public class LastRequestCountForJobScheduledGroupRequestExecutionUpdaterHandler implements ScheduledGroupRequestExecutionUpdaterHandler {
	@Override
	public String handle(ScheduledGroupRequestExecutionBundle execution, YukonUserContext userContext) {
	    int totalCount = execution.getTotalCount();
	    return String.valueOf(totalCount);
	}
	@Override
	public ScheduledGroupCommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
		return ScheduledGroupCommandRequestExecutionUpdaterTypeEnum.LAST_REQUEST_COUNT_FOR_JOB;
	}
}
