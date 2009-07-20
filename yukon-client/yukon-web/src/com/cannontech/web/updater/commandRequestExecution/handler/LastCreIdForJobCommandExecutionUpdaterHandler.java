package com.cannontech.web.updater.commandRequestExecution.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupCommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.commandRequestExecution.CommandRequestExecutionUpdaterTypeEnum;

public class LastCreIdForJobCommandExecutionUpdaterHandler implements CommandRequestExecutionUpdaterHandler {

	private ScheduledGroupCommandRequestExecutionDao scheduledGroupCommandRequestExecutionDao;
	
	@Override
	public String handle(int id, YukonUserContext userContext) {

		int lastCreId = 0;
		
		CommandRequestExecution lastCre = scheduledGroupCommandRequestExecutionDao.getLatestCommandRequestExecutionForJobId(id, null);
		if (lastCre != null) {
			lastCreId = lastCre.getId();
		}
		
		return String.valueOf(lastCreId);
	}
	
	@Override
	public CommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
		return CommandRequestExecutionUpdaterTypeEnum.LAST_CRE_ID_FOR_JOB;
	}

	@Autowired
	public void setScheduledGroupCommandRequestExecutionDao(
			ScheduledGroupCommandRequestExecutionDao scheduledGroupCommandRequestExecutionDao) {
		this.scheduledGroupCommandRequestExecutionDao = scheduledGroupCommandRequestExecutionDao;
	}
}
