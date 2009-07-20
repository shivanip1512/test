package com.cannontech.web.updater.commandRequestExecution.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupCommandRequestExecutionDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.commandRequestExecution.CommandRequestExecutionUpdaterTypeEnum;

public class LastSuccessResultsCountForJobCommandExecutionUpdaterHandler implements CommandRequestExecutionUpdaterHandler {

	private ScheduledGroupCommandRequestExecutionDao scheduledGroupCommandRequestExecutionDao;
	
	@Override
	public String handle(int id, YukonUserContext userContext) {

		int count = scheduledGroupCommandRequestExecutionDao.getLatestSuccessCountForJobId(id);
		return String.valueOf(count);
	}
	
	@Override
	public CommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
		return CommandRequestExecutionUpdaterTypeEnum.LAST_SUCCESS_RESULTS_COUNT_FOR_JOB;
	}

	@Autowired
	public void setScheduledGroupCommandRequestExecutionDao(
			ScheduledGroupCommandRequestExecutionDao scheduledGroupCommandRequestExecutionDao) {
		this.scheduledGroupCommandRequestExecutionDao = scheduledGroupCommandRequestExecutionDao;
	}
}
