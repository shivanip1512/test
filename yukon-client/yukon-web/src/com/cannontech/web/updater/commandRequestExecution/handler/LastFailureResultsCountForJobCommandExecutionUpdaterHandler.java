package com.cannontech.web.updater.commandRequestExecution.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.commandRequestExecution.CommandRequestExecutionUpdaterTypeEnum;

public class LastFailureResultsCountForJobCommandExecutionUpdaterHandler implements CommandRequestExecutionUpdaterHandler {

	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
	
	@Override
	public String handle(int id, YukonUserContext userContext) {

	    int count = 0;
        CommandRequestExecution latestCre = scheduledGroupRequestExecutionDao.findLatestCommandRequestExecutionForJobId(id, null);
        
        if (latestCre != null) {
            count = commandRequestExecutionResultDao.getFailCountByExecutionId(latestCre.getId());
        }
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
	
	@Autowired
    public void setCommandRequestExecutionResultDao(CommandRequestExecutionResultDao commandRequestExecutionResultDao) {
        this.commandRequestExecutionResultDao = commandRequestExecutionResultDao;
    }
}
