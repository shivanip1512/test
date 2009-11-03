package com.cannontech.web.updater.job.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionStatus;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.job.JobUpdaterTypeEnum;

public class StateTextJobUpdaterHandler implements JobUpdaterHandler {

	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	private YukonUserContextMessageSourceResolver messageSourceResolver;
	
	@Override
	public String handle(int jobId, YukonUserContext userContext) {

		ScheduledGroupRequestExecutionStatus state = scheduledGroupRequestExecutionDao.getStatusByJobId(jobId);
		
		return messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(state);
	}
	
	@Override
	public JobUpdaterTypeEnum getUpdaterType() {
		return JobUpdaterTypeEnum.STATE_TEXT;
	}
	
	@Autowired
	public void setScheduledGroupRequestExecutionDao(ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao) {
		this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
	}
	
	@Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}
