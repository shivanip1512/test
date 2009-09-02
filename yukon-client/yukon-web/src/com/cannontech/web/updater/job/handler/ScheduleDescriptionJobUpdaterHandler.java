package com.cannontech.web.updater.job.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.updater.job.JobUpdaterTypeEnum;

public class ScheduleDescriptionJobUpdaterHandler implements JobUpdaterHandler {

	private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
	private CronExpressionTagService cronExpressionTagService;
	
	@Override
	public String handle(int jobId, YukonUserContext userContext) {

		String scheduleDescription = "N/A";
		
		try {
			
			ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(jobId);
			String cronString = job.getCronString();
			scheduleDescription = cronExpressionTagService.getDescription(cronString, userContext);
			
		} catch (IllegalArgumentException e) {
			// N/A
		} catch (EmptyResultDataAccessException e) {
			// N/A
		}
		
		return scheduleDescription;
	}
	
	@Override
	public JobUpdaterTypeEnum getUpdaterType() {
		return JobUpdaterTypeEnum.SCHEDULE_DESCRIPTION;
	}
	
	@Autowired
	public void setScheduledRepeatingJobDao(ScheduledRepeatingJobDao scheduledRepeatingJobDao) {
		this.scheduledRepeatingJobDao = scheduledRepeatingJobDao;
	}
	
	@Autowired
	public void setCronExpressionTagService(CronExpressionTagService cronExpressionTagService) {
        this.cronExpressionTagService = cronExpressionTagService;
    }
}
