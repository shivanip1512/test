package com.cannontech.web.updater.scheduledGroupRequestExecution.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.scheduledGroupRequestExecution.ScheduledGroupCommandRequestExecutionUpdaterTypeEnum;

public class LastCreIdForJobScheduledGroupRequestExecutionUpdaterHandler implements ScheduledGroupRequestExecutionUpdaterHandler {

	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	
	@Override
	public String handle(int id, YukonUserContext userContext) {

		int lastCreId = 0;
		
		CommandRequestExecution lastCre = scheduledGroupRequestExecutionDao.findLatestCommandRequestExecutionForJobId(id, null);
		if (lastCre != null) {
			lastCreId = lastCre.getId();
		}
		
		return String.valueOf(lastCreId);
	}
	
	@Override
	public ScheduledGroupCommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
		return ScheduledGroupCommandRequestExecutionUpdaterTypeEnum.LAST_CRE_ID_FOR_JOB;
	}

	@Autowired
	public void setScheduledGroupRequestExecutionDao(
			ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao) {
		this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
	}
}
