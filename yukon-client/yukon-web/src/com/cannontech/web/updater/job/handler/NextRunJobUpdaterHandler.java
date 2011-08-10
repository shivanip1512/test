package com.cannontech.web.updater.job.handler;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.job.JobUpdaterTypeEnum;

public class NextRunJobUpdaterHandler implements JobUpdaterHandler {

	private JobManager jobManager;
	private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
	private DateFormattingService dateFormattingService;
	
	@Override
	public String handle(int jobId, YukonUserContext userContext) {

	    ScheduledRepeatingJob job = null;
	    try {
	        job = scheduledRepeatingJobDao.getById(jobId);
	    } catch (EmptyResultDataAccessException e) {
	        //silently returning since this job could have just been deleted
	        return null;
	    }
	    
		Date nextRun;
		try {
			nextRun = jobManager.getNextRuntime(job, new Date());
		} catch (ScheduleException e) {
			nextRun = null;
		}
		
		String dateStr = "N/A";
		if (nextRun != null) {
			dateStr = dateFormattingService.format(nextRun, DateFormatEnum.DATEHM, userContext);
		}
		
		return dateStr;
	}
	
	@Override
	public JobUpdaterTypeEnum getUpdaterType() {
		return JobUpdaterTypeEnum.NEXT_RUN_DATE;
	}
	
	@Resource(name="jobManager")
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}
	
	@Autowired
	public void setScheduledRepeatingJobDao(ScheduledRepeatingJobDao scheduledRepeatingJobDao) {
		this.scheduledRepeatingJobDao = scheduledRepeatingJobDao;
	}
	
	@Autowired
	public void setDateFormattingService(DateFormattingService dateFormattingService) {
		this.dateFormattingService = dateFormattingService;
	}
}
