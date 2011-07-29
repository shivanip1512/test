package com.cannontech.web.updater.scheduledGroupRequestExecution.handler;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionBundle;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.scheduledGroupRequestExecution.ScheduledGroupCommandRequestExecutionUpdaterTypeEnum;

public class LastSuccessResultsCountForJobScheduledGroupRequestExecutionUpdaterHandler implements ScheduledGroupRequestExecutionUpdaterHandler {
	@Override
	public String handle(ScheduledGroupRequestExecutionBundle execution, YukonUserContext userContext) {
	    int successCount = execution.getSuccessCount();
	    return String.valueOf(successCount);
	}
	@Override
	public ScheduledGroupCommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
		return ScheduledGroupCommandRequestExecutionUpdaterTypeEnum.LAST_SUCCESS_RESULTS_COUNT_FOR_JOB;
	}
}
