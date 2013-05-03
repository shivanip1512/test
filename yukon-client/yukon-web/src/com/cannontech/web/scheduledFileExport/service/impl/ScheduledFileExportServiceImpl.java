package com.cannontech.web.scheduledFileExport.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.scheduledFileExport.ScheduledExportType;
import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.scheduledFileExport.ScheduledFileExportJobData;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledArchivedDataFileExportTask;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledBillingFileExportTask;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledFileExportTask;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledMeterEventsFileExportTask;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledWaterLeakFileExportTask;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.google.common.collect.Maps;

public class ScheduledFileExportServiceImpl implements ScheduledFileExportService {
	@Autowired private JobManager jobManager;
	@Autowired private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
	@Autowired private CronExpressionTagService cronExpressionTagService;
	@Resource(name="scheduledBillingFileExportJobDefinition")
	private YukonJobDefinition<ScheduledBillingFileExportTask> scheduledBillingFileExportJobDefinition;
	@Resource(name="scheduledArchivedDataFileExportJobDefinition")
	private YukonJobDefinition<ScheduledArchivedDataFileExportTask> scheduledArchivedDataFileExportJobDefinition;
	@Resource(name="scheduledWaterLeakFileExportJobDefinition")
	private YukonJobDefinition<ScheduledWaterLeakFileExportTask> scheduledWaterLeakFileExportJobDefinition;
	@Resource(name="scheduledMeterEventsFileExportJobDefinition")
	private YukonJobDefinition<ScheduledMeterEventsFileExportTask> scheduledMeterEventsFileExportJobDefinition;

	private Logger log = YukonLogManager.getLogger(ScheduledFileExportServiceImpl.class);
	
	private Map<ScheduledExportType, YukonJobDefinition<? extends ScheduledFileExportTask>> typeToJobDefinitionMap; 
	
	@PostConstruct
	public void init() {
		typeToJobDefinitionMap = Maps.newEnumMap(ScheduledExportType.class);
		typeToJobDefinitionMap.put(ScheduledExportType.BILLING, scheduledBillingFileExportJobDefinition);
		typeToJobDefinitionMap.put(ScheduledExportType.ARCHIVED_DATA_EXPORT, scheduledArchivedDataFileExportJobDefinition);
		typeToJobDefinitionMap.put(ScheduledExportType.WATER_LEAK, scheduledWaterLeakFileExportJobDefinition);
		typeToJobDefinitionMap.put(ScheduledExportType.METER_EVENT, scheduledMeterEventsFileExportJobDefinition);
	}
	
	@Override
	public YukonJob scheduleFileExport(ScheduledFileExportData data, YukonUserContext userContext, HttpServletRequest request) {
		//Build default url for notifications
	    String defaultYukonExternalUrl = ServletUtil.getDefaultYukonExternalUrl(request);		
		//Find the appropriate job definition for this export type
		YukonJobDefinition<? extends ScheduledFileExportTask> jobDefinition = getJobDefinition(data.getExportType());
		//Create task
		ScheduledFileExportTask task = getTask(jobDefinition, data);
		task.setDefaultYukonExternalUrl(defaultYukonExternalUrl);
		//Schedule the job
		YukonJob job = jobManager.scheduleJob(jobDefinition, task, data.getScheduleCronString(), userContext);
		logSchedulingAction(data, false);
		return job;
	}
	
	public String getDefaultYukonExternalUrl(HttpServletRequest request) {
	    String defaultYukonExternalUrl = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80) {
            defaultYukonExternalUrl += ":" + request.getServerPort();
        }
        return defaultYukonExternalUrl;
	}
	
	@Override
	public YukonJob updateFileExport(ScheduledFileExportData data, YukonUserContext userContext, HttpServletRequest request, int jobId) {
		//Build default url for notifications
		String defaultYukonExternalUrl = ServletUtil.getDefaultYukonExternalUrl(request);
		//Find the appropriate job definition for this export type
		YukonJobDefinition<? extends ScheduledFileExportTask> jobDefinition = getJobDefinition(data.getExportType());
		//Create task and supply parameters
		ScheduledFileExportTask task = getTask(jobDefinition, data);
		task.setDefaultYukonExternalUrl(defaultYukonExternalUrl);
		//Update the job
		YukonJob job = jobManager.replaceScheduledJob(jobId, jobDefinition, task, data.getScheduleCronString(), userContext);
		logSchedulingAction(data, true);
		return job;
	}
	
	@Override
	public List<ScheduledRepeatingJob> getJobsByType(ScheduledExportType type) {
		YukonJobDefinition<? extends ScheduledFileExportTask> jobDefinition = getJobDefinition(type);
		return jobManager.getNotDeletedRepeatingJobsByDefinition(jobDefinition);
	}
	
	@Override
	public int deleteAdeJobsByFormatId(int formatId) {
		int count = 0;
		List<ScheduledRepeatingJob> jobs = getJobsByType(ScheduledExportType.ARCHIVED_DATA_EXPORT);
        for(ScheduledRepeatingJob job : jobs) {
        	ScheduledArchivedDataFileExportTask task = (ScheduledArchivedDataFileExportTask) jobManager.instantiateTask(job);
        	if(task.getFormatId() == formatId) {
        		jobManager.deleteJob(job);
        		count++;
        	}
        }
        return count;
	}
	
	@Override
	public int deleteBillingJobsByFormatId(int formatId) {
		int count = 0;
		List<ScheduledRepeatingJob> jobs = getJobsByType(ScheduledExportType.BILLING);
		for(ScheduledRepeatingJob job : jobs) {
			ScheduledBillingFileExportTask task = (ScheduledBillingFileExportTask) jobManager.instantiateTask(job);
			if(task.getFileFormatId() == formatId) {
				jobManager.deleteJob(job);
				count++;
			}
		}
		return count;
	}
	
	@Override
	public ScheduledFileExportJobData getExportJobData(ScheduledRepeatingJob job) {
		return new ScheduledFileExportJobDataImpl(job);
	}
	
	public YukonJobDefinition<? extends ScheduledFileExportTask> getJobDefinition(ScheduledExportType type) {
		YukonJobDefinition<? extends ScheduledFileExportTask> jobDefinition = typeToJobDefinitionMap.get(type);
		if(jobDefinition == null) {
			throw new IllegalArgumentException("Cannot schedule file export task of type \"" + type);
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
		String action = isUpdate ? "Updated" : "Scheduled";
		
		log.debug(action + " new file export job.");
		log.debug("Name: " + data.getScheduleName());
		log.debug("Append date to file name: " + data.isAppendDateToFileName());
		log.debug("Export path: " + data.getExportPath());
		log.debug("Export file: " + data.getExportFileName());
		log.debug("Notification email addresses: " + data.getNotificationEmailAddresses());
		log.debug("Generation parameters: " + data.getParameters());
	}
	
	private class ScheduledFileExportJobDataImpl implements ScheduledFileExportJobData {
		private int jobId;
		private String name;
		private String cronString;
		
		public ScheduledFileExportJobDataImpl(ScheduledRepeatingJob job) {
			jobId = job.getId();
			
			cronString = cronExpressionTagService.getDescription(job.getCronString(), job.getUserContext());
			
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
		public int compareTo(ScheduledFileExportJobData other) {
			return this.name.compareTo(other.getName());
		}
	}
}
