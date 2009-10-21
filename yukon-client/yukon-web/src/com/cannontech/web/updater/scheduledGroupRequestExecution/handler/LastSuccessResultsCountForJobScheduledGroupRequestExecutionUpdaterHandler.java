package com.cannontech.web.updater.scheduledGroupRequestExecution.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.scheduledGroupRequestExecution.ScheduledGroupCommandRequestExecutionUpdaterTypeEnum;

public class LastSuccessResultsCountForJobScheduledGroupRequestExecutionUpdaterHandler implements ScheduledGroupRequestExecutionUpdaterHandler {

	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	
	@Override
	public String handle(int id, YukonUserContext userContext) {

	    int count = scheduledGroupRequestExecutionDao.getLatestSuccessCountByJobId(id);

	    return String.valueOf(count);
	}
	
	@Override
	public ScheduledGroupCommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
		return ScheduledGroupCommandRequestExecutionUpdaterTypeEnum.LAST_SUCCESS_RESULTS_COUNT_FOR_JOB;
	}

	@Autowired
	public void setScheduledGroupRequestExecutionDao(
			ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao) {
		this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
	}
}
