package com.cannontech.web.scheduledFileExport.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.scheduledFileExport.ScheduledExportType;
import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.scheduledFileExport.ScheduledBillingJobData;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledAdeAllFileExportTask;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledAdeFilteredFileExportTask;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledBillingFileExportTask;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledFileExportTask;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Maps;

public class ScheduledFileExportServiceImpl implements ScheduledFileExportService {
	@Autowired private JobManager jobManager;
	@Autowired private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
	@Autowired private CronExpressionTagService cronExpressionTagService;
	@Resource(name="scheduledBillingFileExportJobDefinition")
	private YukonJobDefinition<ScheduledBillingFileExportTask> scheduledBillingFileExportJobDefinition;
	@Resource(name="scheduledAdeFilteredFileExportJobDefinition")
	private YukonJobDefinition<ScheduledAdeFilteredFileExportTask> scheduledAdeFilteredFileExportJobDefinition;
	@Resource(name="scheduledAdeAllFileExportJobDefinition")
	private YukonJobDefinition<ScheduledAdeAllFileExportTask> scheduledAdeAllFileExportJobDefinition;
	
	private Logger log = YukonLogManager.getLogger(ScheduledFileExportServiceImpl.class);
	
	private Map<ScheduledExportType, YukonJobDefinition<? extends ScheduledFileExportTask>> typeToJobDefinitionMap; 
	
	@PostConstruct
	public void init() {
		typeToJobDefinitionMap = Maps.newEnumMap(ScheduledExportType.class);
		typeToJobDefinitionMap.put(ScheduledExportType.BILLING, scheduledBillingFileExportJobDefinition);
		typeToJobDefinitionMap.put(ScheduledExportType.ARCHIVED_DATA_EXPORT_FILTERED, scheduledAdeFilteredFileExportJobDefinition);
		typeToJobDefinitionMap.put(ScheduledExportType.ARCHIVED_DATA_EXPORT_ALL, scheduledAdeAllFileExportJobDefinition);
	}
	
	@Override
	public YukonJob scheduleFileExport(ScheduledFileExportData data, YukonUserContext userContext) {
		//Find the appropriate job definition for this export type
		YukonJobDefinition<? extends ScheduledFileExportTask> jobDefinition = getJobDefinition(data);
		//Create task
		ScheduledFileExportTask task = getTask(jobDefinition, data);
		//Schedule the job
		YukonJob job = jobManager.scheduleJob(jobDefinition, task, data.getScheduleCronString(), userContext);
		logSchedulingAction(data, false);
		return job;
	}
	
	@Override
	public YukonJob updateFileExport(ScheduledFileExportData data, YukonUserContext userContext, int jobId) {
		//Find the appropriate job definition for this export type
		YukonJobDefinition<? extends ScheduledFileExportTask> jobDefinition = getJobDefinition(data);
		//Create task and supply parameters
		ScheduledFileExportTask task = getTask(jobDefinition, data);
		//Update the job
		YukonJob job = jobManager.replaceScheduledJob(jobId, jobDefinition, task, data.getScheduleCronString(), userContext);
		logSchedulingAction(data, true);
		return job;
	}
	
	@Override
	public List<ScheduledRepeatingJob> getBillingExportJobs() {
		return jobManager.getNotDeletedRepeatingJobsByDefinition(scheduledBillingFileExportJobDefinition);
	}
	
	@Override
	public ScheduledBillingJobData getBillingJobData(ScheduledRepeatingJob job) {
		return new ScheduledBillingJobDataImpl(job);
	}
	
	public YukonJobDefinition<? extends ScheduledFileExportTask> getJobDefinition(ScheduledFileExportData data) {
		YukonJobDefinition<? extends ScheduledFileExportTask> jobDefinition = typeToJobDefinitionMap.get(data.getExportType());
		if(jobDefinition == null) {
			throw new IllegalArgumentException("Cannot schedule file export task of type \"" + data.getExportType());
		}
		return jobDefinition;
	}
	
	private ScheduledFileExportTask getTask(YukonJobDefinition<? extends ScheduledFileExportTask> jobDefinition, ScheduledFileExportData data) {
		ScheduledFileExportTask task = jobDefinition.createBean();
		task.setName(data.getScheduleName());
		task.setAppendDateToFileName(data.isAppendDateToFileName());
		task.setExportFileName(data.getExportFileName());
		task.setExportPath(data.getExportPath());
		task.setNotificationEmailAddresses(data.getNotificationEmailAddresses());
		task.setFileGenerationParameters(data.getParameters());
		return task;
	}
	
	private void logSchedulingAction(ScheduledFileExportData data, boolean isUpdate) {
		String action;
		if(isUpdate) action = "Updated";
		else action = "Scheduled";
		
		log.debug(action + " new file export job.");
		log.debug("Name: " + data.getScheduleName());
		log.debug("Append date to file name: " + data.isAppendDateToFileName());
		log.debug("Export path: " + data.getExportPath());
		log.debug("Export file: " + data.getExportFileName());
		log.debug("Notification email addresses: " + data.getNotificationEmailAddresses());
		log.debug("Generation parameters: " + data.getParameters());
	}
	
	private class ScheduledBillingJobDataImpl implements ScheduledBillingJobData {
		private int jobId;
		private String name;
		private String cronString;
		private Date nextRun = null;
		
		public ScheduledBillingJobDataImpl(ScheduledRepeatingJob job) {
			jobId = job.getId();
			
			cronString = cronExpressionTagService.getDescription(job.getCronString(), job.getUserContext());
			
			try {
				nextRun = jobManager.getNextRuntime(job, new Date());
			} catch(ScheduleException e) { 
				//just leave null
			}
			
			ScheduledFileExportTask task = (ScheduledFileExportTask) jobManager.instantiateTask(job);
			this.name = task.getName();
		}
		
		@Override
		public int getId() {
			return jobId;
		}
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public String getCronString() {
			return cronString;
		}
		
		@Override
		public Date getNextRun() {
			return nextRun;
		}
		
		@Override
		public int compareTo(ScheduledBillingJobData other) {
			return this.name.compareTo(other.getName());
		}
	}
}
