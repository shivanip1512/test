package com.cannontech.web.updater.commandRequestExecution.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultsDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.commandRequestExecution.CommandRequestExecutionUpdaterTypeEnum;

public class SuccessResultsCountCommandExecutionUpdaterHandler implements CommandRequestExecutionUpdaterHandler {

	private CommandRequestExecutionResultsDao commandRequestExecutionResultsDao;
	
	@Override
	public String handle(int id, YukonUserContext userContext) {

		int count = commandRequestExecutionResultsDao.getSucessCountByExecutionId(id);
		return String.valueOf(count);
	}
	
	@Override
	public CommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
		return CommandRequestExecutionUpdaterTypeEnum.SUCCESS_RESULTS_COUNT;
	}

	@Autowired
	public void setCommandRequestExecutionResultsDao(
			CommandRequestExecutionResultsDao commandRequestExecutionResultsDao) {
		this.commandRequestExecutionResultsDao = commandRequestExecutionResultsDao;
	}
}
