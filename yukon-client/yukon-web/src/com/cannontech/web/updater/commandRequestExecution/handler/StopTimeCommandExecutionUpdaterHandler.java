package com.cannontech.web.updater.commandRequestExecution.handler;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.commandRequestExecution.CommandRequestExecutionUpdaterTypeEnum;

public class StopTimeCommandExecutionUpdaterHandler implements CommandRequestExecutionUpdaterHandler {

	private CommandRequestExecutionDao commandRequestExecutionDao;
	private DateFormattingService dateFormattingService;
	
	@Override
	public String handle(int id, YukonUserContext userContext) {

		Date stopTime =commandRequestExecutionDao.getStopTime(id);
		
		String dateStr = "In Progress";
		if (stopTime != null) {
			dateStr = dateFormattingService.format(stopTime, DateFormatEnum.DATEHM, userContext);
		}
		
		return dateStr;
	}
	
	@Override
	public CommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
		return CommandRequestExecutionUpdaterTypeEnum.STOP_TIME;
	}

	@Autowired
	public void setCommandRequestExecutionDao(CommandRequestExecutionDao commandRequestExecutionDao) {
		this.commandRequestExecutionDao = commandRequestExecutionDao;
	}
	
	@Autowired
	public void setDateFormattingService(DateFormattingService dateFormattingService) {
		this.dateFormattingService = dateFormattingService;
	}
}
