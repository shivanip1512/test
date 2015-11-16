package com.cannontech.web.updater.scheduledGroupRequestExecution.handler;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionBundle;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.scheduledGroupRequestExecution.ScheduledGroupCommandRequestExecutionUpdaterTypeEnum;

public class LastRunDateHandler implements ScheduledGroupRequestExecutionUpdaterHandler {
    @Autowired private JobStatusDao jobStatusDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
	
	@Override
	public String handle(ScheduledGroupRequestExecutionBundle execution, YukonUserContext userContext) {
		int jobId = execution.getJobId();

		Date lastRunDate = jobStatusDao.findJobLastCompletedRunDate(jobId);
		if (lastRunDate != null) {
		    return dateFormattingService.format(lastRunDate, DateFormatEnum.DATEHM, userContext);
		}

		// This can happen if the schedule has never been run or if it is currently running.
		MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
		return messageSourceAccessor.getMessage("yukon.common.na");
	}
	
	@Override
	public ScheduledGroupCommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
		return ScheduledGroupCommandRequestExecutionUpdaterTypeEnum.LAST_RUN_DATE;
	}
}
