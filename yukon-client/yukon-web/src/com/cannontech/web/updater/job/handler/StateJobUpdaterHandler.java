package com.cannontech.web.updater.job.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionStatus;
import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionStatusService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.job.JobUpdaterTypeEnum;

public class StateJobUpdaterHandler implements JobUpdaterHandler {
    private ScheduledGroupRequestExecutionStatusService executionStatusService;
	
	@Override
	public String handle(int jobId, YukonUserContext userContext) {
	    ScheduledGroupRequestExecutionStatus status = executionStatusService.getStatus(jobId);
		return status.name();
	}
	
	@Override
	public JobUpdaterTypeEnum getUpdaterType() {
		return JobUpdaterTypeEnum.STATE;
	}
	
	@Autowired
	public void setExecutionStatusService(ScheduledGroupRequestExecutionStatusService executionStatusService) {
        this.executionStatusService = executionStatusService;
    }
}
