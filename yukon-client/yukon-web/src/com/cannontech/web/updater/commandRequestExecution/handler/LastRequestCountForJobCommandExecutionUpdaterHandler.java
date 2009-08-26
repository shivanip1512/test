package com.cannontech.web.updater.commandRequestExecution.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.commandRequestExecution.CommandRequestExecutionUpdaterTypeEnum;

public class LastRequestCountForJobCommandExecutionUpdaterHandler implements CommandRequestExecutionUpdaterHandler {

	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	private CommandRequestExecutionDao commandRequestExecutionDao;
	
	@Override
	public String handle(int id, YukonUserContext userContext) {

	    int count = 0;
        CommandRequestExecution latestCre = scheduledGroupRequestExecutionDao.findLatestCommandRequestExecutionForJobId(id, null);
        if (latestCre != null) {
            count = commandRequestExecutionDao.getRequestCountById(latestCre.getId());
        }
		return String.valueOf(count);
	}
	
	@Override
	public CommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
		return CommandRequestExecutionUpdaterTypeEnum.LAST_REQUEST_COUNT_FOR_JOB;
	}

	@Autowired
	public void setScheduledGroupRequestExecutionDao(
			ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao) {
		this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
	}
	
	@Autowired
    public void setCommandRequestExecutionDao(CommandRequestExecutionDao commandRequestExecutionDao) {
        this.commandRequestExecutionDao = commandRequestExecutionDao;
    }
}
