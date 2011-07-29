package com.cannontech.web.updater.job.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionStatus;
import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionStatusService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.job.JobUpdaterTypeEnum;

public class StateTextJobUpdaterHandler implements JobUpdaterHandler {

	private ScheduledGroupRequestExecutionStatusService executionStatusService;
	private YukonUserContextMessageSourceResolver messageSourceResolver;
	
	@Override
	public String handle(int jobId, YukonUserContext userContext) {

		ScheduledGroupRequestExecutionStatus state = executionStatusService.getStatus(jobId);
		
		return messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(state);
	}
	
	@Override
	public JobUpdaterTypeEnum getUpdaterType() {
		return JobUpdaterTypeEnum.STATE_TEXT;
	}
	
	@Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
	@Autowired
	public void setExecutionStatusService(ScheduledGroupRequestExecutionStatusService executionStatusService) {
        this.executionStatusService = executionStatusService;
    }
}
