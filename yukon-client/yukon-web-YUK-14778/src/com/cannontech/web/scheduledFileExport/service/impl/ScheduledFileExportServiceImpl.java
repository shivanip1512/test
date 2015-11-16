package com.cannontech.web.scheduledFileExport.service.impl;

import java.util.Collections;
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
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.scheduledFileExport.ScheduledFileExportJobData;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.cannontech.web.scheduledFileExport.tasks.PersistedFormatTask;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledArchivedDataFileExportTask;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledBillingFileExportTask;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledFileExportTask;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledMeterEventsFileExportTask;
import com.cannontech.web.scheduledFileExport.tasks.ScheduledWaterLeakFileExportTask;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ScheduledFileExportServiceImpl implements ScheduledFileExportService {
    private Logger log = YukonLogManager.getLogger(ScheduledFileExportServiceImpl.class);

    @Autowired private JobManager jobManager;
    @Autowired private JobStatusDao jobStatusDao;
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
    public YukonJob scheduleFileExport(ScheduledFileExportData data, YukonUserContext userContext,
                                       HttpServletRequest request) {
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
    public YukonJob updateFileExport(ScheduledFileExportData data, YukonUserContext userContext,
                                     HttpServletRequest request, int jobId) {
        //Build default url for notifications
        String defaultYukonExternalUrl = ServletUtil.getDefaultYukonExternalUrl(request);
        //Find the appropriate job definition for this export type
        YukonJobDefinition<? extends ScheduledFileExportTask> jobDefinition = getJobDefinition(data.getExportType());
        //Create task and supply parameters
        ScheduledFileExportTask task = getTask(jobDefinition, data);
        task.setDefaultYukonExternalUrl(defaultYukonExternalUrl);
        //Update the job
        YukonJob job = jobManager.replaceScheduledJob(jobId, jobDefinition, task, 
                                                      data.getScheduleCronString(), userContext);
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
        return deleteJobsByFormatId(formatId, ScheduledExportType.ARCHIVED_DATA_EXPORT);
    }

    @Override
    public int deleteBillingJobsByFormatId(int formatId) {
        return deleteJobsByFormatId(formatId, ScheduledExportType.BILLING);
    }

    @Override
    public List<ScheduledFileExportJobData> getScheduledFileExportJobData(ScheduledExportType scheduleType) {

        List<ScheduledRepeatingJob> exportJobs = getJobsByType(scheduleType);
        List<ScheduledFileExportJobData> jobs = Lists.newArrayListWithCapacity(exportJobs.size());
        for(ScheduledRepeatingJob job : exportJobs) {
            jobs.add(getExportJobData(job));
        }
        Collections.sort(jobs);

        return jobs;
    }

    private int deleteJobsByFormatId(int formatId, ScheduledExportType type) {
        if(!type.isPersistedFormat()) {
            throw new IllegalArgumentException("ScheduledExportType of " + type + " cannot be deleted by format id.");
        }

        int count = 0;
        List<ScheduledRepeatingJob> jobs = getJobsByType(type);
        for(ScheduledRepeatingJob job : jobs) {
            PersistedFormatTask task = (PersistedFormatTask) jobManager.instantiateTask(job);
            if(task.getFormatId() == formatId) {
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

    private YukonJobDefinition<? extends ScheduledFileExportTask> getJobDefinition(ScheduledExportType type) {
        YukonJobDefinition<? extends ScheduledFileExportTask> jobDefinition = typeToJobDefinitionMap.get(type);
        if(jobDefinition == null) {
            throw new IllegalArgumentException("Cannot schedule file export task of type \"" + type);
        }
        return jobDefinition;
    }

    private ScheduledFileExportTask getTask(YukonJobDefinition<? extends ScheduledFileExportTask> jobDefinition,
                                            ScheduledFileExportData data) {
        ScheduledFileExportTask task = jobDefinition.createBean();
        task.setName(data.getScheduleName());
        task.setAppendDateToFileName(data.isAppendDateToFileName());
        task.setExportFileName(data.getExportFileName());
        task.setExportPath(data.getExportPath());
        task.setNotificationEmailAddresses(data.getNotificationEmailAddresses());
        task.setFileGenerationParameters(data.getParameters());
        task.setTimestampPatternField(data.getTimestampPatternField());
        task.setOverrideFileExtension(data.isOverrideFileExtension());
        task.setExportFileExtension(data.getExportFileExtension());
        task.setIncludeExportCopy(data.isIncludeExportCopy());
        task.setSendEmail(data.isSendEmail());
        
        return task;
    }

    private void logSchedulingAction(ScheduledFileExportData data, boolean isUpdate) {
        String action = isUpdate ? "Updated" : "Scheduled";

        log.debug(action + " new file export job.");
        log.debug("Name: " + data.getScheduleName());
        log.debug("Export file: " + data.getExportFileName());
        log.debug("Append date to file name: " + data.isAppendDateToFileName());
        log.debug("Timestamp Pattern: " + data.getTimestampPatternField());
        log.debug("Override File Extension: " + data.isOverrideFileExtension());
        log.debug("File extension: " + data.getExportFileExtension());
        log.debug("Include file copy: " + data.isIncludeExportCopy());
        log.debug("Export path: " + data.getExportPath());
        log.debug("Notification email addresses: " + data.getNotificationEmailAddresses());
        log.debug("Generation parameters: " + data.getParameters());
    }

    private class ScheduledFileExportJobDataImpl implements ScheduledFileExportJobData {
        private int jobId;
        private String name;
        private String cronString;
        private int jobGroupId;
        private JobState jobState;
        
        public ScheduledFileExportJobDataImpl(ScheduledRepeatingJob job) {
            jobId = job.getId();
            jobGroupId = job.getJobGroupId();
            cronString = cronExpressionTagService.getDescription(job.getCronString(), job.getUserContext());
            ScheduledFileExportTask task = (ScheduledFileExportTask) jobManager.instantiateTask(job);
            this.name = task.getName();
            JobStatus<YukonJob> status = jobStatusDao.findLatestStatusByJobId(jobId);

            JobDisabledStatus jobDisabledStatus = jobManager.getJobDisabledStatus(jobId);
            jobState = JobState.of(jobDisabledStatus, status);
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

        @Override
        public int getJobGroupId() {
            return jobGroupId;
        }

        @Override
        public JobState getJobState() {
            return jobState;
        }

    }
}
