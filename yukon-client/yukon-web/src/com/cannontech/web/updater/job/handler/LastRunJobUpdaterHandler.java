package com.cannontech.web.updater.job.handler;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.job.JobUpdaterTypeEnum;

public class LastRunJobUpdaterHandler implements JobUpdaterHandler {

	private JobStatusDao jobStatusDao;
	private DateFormattingService dateFormattingService;
	
	@Override
	public String handle(int jobId, YukonUserContext userContext) {

		Date lastRun = jobStatusDao.getJobLastSuccessfulRunDate(jobId);

		String dateStr = "N/A";
		if (lastRun != null) {
			dateStr = dateFormattingService.formatDate(lastRun, DateFormatEnum.DATEHM, userContext);
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
