package com.cannontech.web.updater.scheduledGroupRequestExecution.handler;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionBundle;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.scheduledGroupRequestExecution.ScheduledGroupCommandRequestExecutionUpdaterTypeEnum;

public class LastFailureCountHandler implements ScheduledGroupRequestExecutionUpdaterHandler {
	@Override
	public String handle(ScheduledGroupRequestExecutionBundle execution, YukonUserContext userContext) {
	    int failureCount = execution.getExecutionCounts().getFailureCount();
		return String.valueOf(failureCount);
	}
	
	@Override
	public ScheduledGroupCommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
		return ScheduledGroupCommandRequestExecutionUpdaterTypeEnum.LAST_FAILURE_RESULTS_COUNT_FOR_JOB;
	}
}
