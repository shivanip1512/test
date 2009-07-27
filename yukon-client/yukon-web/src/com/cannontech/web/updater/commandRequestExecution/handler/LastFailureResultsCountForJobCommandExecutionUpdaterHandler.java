package com.cannontech.web.updater.commandRequestExecution.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.commandRequestExecution.CommandRequestExecutionUpdaterTypeEnum;

public class LastFailureResultsCountForJobCommandExecutionUpdaterHandler implements CommandRequestExecutionUpdaterHandler {

	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	
	@Override
	public String handle(int id, YukonUserContext userContext) {

		int count = scheduledGroupRequestExecutionDao.getLatestFailCountForJobId(id);
		return String.valueOf(count);
	}
	
	@Override
	public CommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
		return CommandRequestExecutionUpdaterTypeEnum.LAST_FAILURE_RESULTS_COUNT_FOR_JOB;
	}

	@Autowired
	public void setScheduledGroupRequestExecutionDao(
			ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao) {
		this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
	}
}
