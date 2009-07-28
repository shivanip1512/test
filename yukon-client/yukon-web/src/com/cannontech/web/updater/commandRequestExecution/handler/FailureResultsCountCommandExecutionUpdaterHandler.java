package com.cannontech.web.updater.commandRequestExecution.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.commandRequestExecution.CommandRequestExecutionUpdaterTypeEnum;

public class FailureResultsCountCommandExecutionUpdaterHandler implements CommandRequestExecutionUpdaterHandler {

	private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
	
	@Override
	public String handle(int id, YukonUserContext userContext) {

		int count = commandRequestExecutionResultDao.getFailCountByExecutionId(id);
		return String.valueOf(count);
	}
	
	@Override
	public CommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
		return CommandRequestExecutionUpdaterTypeEnum.FAILURE_RESULTS_COUNT;
	}

	@Autowired
	public void setCommandRequestExecutionResultDao(
			CommandRequestExecutionResultDao commandRequestExecutionResultDao) {
		this.commandRequestExecutionResultDao = commandRequestExecutionResultDao;
	}
}
