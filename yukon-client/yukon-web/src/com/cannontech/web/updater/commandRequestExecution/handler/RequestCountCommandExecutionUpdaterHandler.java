package com.cannontech.web.updater.commandRequestExecution.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.commandRequestExecution.CommandRequestExecutionUpdaterTypeEnum;

public class RequestCountCommandExecutionUpdaterHandler implements CommandRequestExecutionUpdaterHandler {

	private CommandRequestExecutionDao commandRequestExecutionDao;
	
	@Override
	public String handle(int id, YukonUserContext userContext) {

		int count = commandRequestExecutionDao.getRequestCountByCreId(id);
		return String.valueOf(count);
	}
	
	@Override
	public CommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
		return CommandRequestExecutionUpdaterTypeEnum.REQUEST_COUNT;
	}

	@Autowired
	public void setCommandRequestExecutionDao(CommandRequestExecutionDao commandRequestExecutionDao) {
		this.commandRequestExecutionDao = commandRequestExecutionDao;
	}
}
