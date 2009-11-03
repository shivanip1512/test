package com.cannontech.web.updater.job.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.job.JobUpdaterTypeEnum;

public class StateJobUpdaterHandler implements JobUpdaterHandler {

	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	
	@Override
	public String handle(int jobId, YukonUserContext userContext) {

		return scheduledGroupRequestExecutionDao.getStatusByJobId(jobId).name();
	}
	
	@Override
	public JobUpdaterTypeEnum getUpdaterType() {
		return JobUpdaterTypeEnum.STATE;
	}
	
	@Autowired
	public void setScheduledGroupRequestExecutionDao(ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao) {
		this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
	}
}
