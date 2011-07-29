package com.cannontech.web.updater.scheduledGroupRequestExecution.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionBundle;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.scheduledGroupRequestExecution.ScheduledGroupCommandRequestExecutionUpdaterTypeEnum;

public class CreCountForJobLastCreIdForJobScheduledGroupRequestExecutionUpdaterHandler implements ScheduledGroupRequestExecutionUpdaterHandler {

	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	
	@Override
	public String handle(ScheduledGroupRequestExecutionBundle execution, YukonUserContext userContext) {
	    int jobId = execution.getJobId();
		int count = scheduledGroupRequestExecutionDao.getDistinctCreCountByJobId(jobId, null, null);
		return String.valueOf(count);
	}
	
	@Override
	public ScheduledGroupCommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
		return ScheduledGroupCommandRequestExecutionUpdaterTypeEnum.CRE_COUNT_FOR_JOB;
	}

	@Autowired
	public void setScheduledGroupRequestExecutionDao(
			ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao) {
		this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
	}
}
