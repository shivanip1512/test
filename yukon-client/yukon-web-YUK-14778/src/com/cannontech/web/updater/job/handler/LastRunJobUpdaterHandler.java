package com.cannontech.web.updater.job.handler;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.job.JobUpdaterTypeEnum;

public class LastRunJobUpdaterHandler implements JobUpdaterHandler {

	private JobStatusDao jobStatusDao;
	private DateFormattingService dateFormattingService;
	@Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
	
	@Override
	public String handle(int jobId, YukonUserContext userContext) {

		Date lastRun = jobStatusDao.findJobLastSuccessfulRunDate(jobId);

		String dateStr = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage("yukon.common.na");
		if (lastRun != null) {
			dateStr = dateFormattingService.format(lastRun, DateFormatEnum.DATEHM, userContext);
		}
		
		return dateStr;
	}
	
	@Override
	public JobUpdaterTypeEnum getUpdaterType() {
		return JobUpdaterTypeEnum.LAST_RUN_DATE;
	}
	
	@Autowired
	public void setJobStatusDao(JobStatusDao jobStatusDao) {
		this.jobStatusDao = jobStatusDao;
	}
	
	@Autowired
	public void setDateFormattingService(DateFormattingService dateFormattingService) {
		this.dateFormattingService = dateFormattingService;
	}
}
