package com.cannontech.web.scheduledDataImport.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.scheduledFileImport.ImportType;
import com.cannontech.common.scheduledFileImport.ScheduledDataImport;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.scheduledDataImport.service.ScheduledDataImportService;
import com.cannontech.web.scheduledDataImport.tasks.ScheduledDataImportTask;

public class ScheduledDataImportServiceImpl implements ScheduledDataImportService {

    @Autowired private JobManager jobManager;
    @Autowired @Qualifier("scheduledDataImport") private YukonJobDefinition<ScheduledDataImportTask> scheduledDataImportJobDefinition;
    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private JobStatusDao jobStatusDao;

    private Logger log = YukonLogManager.getLogger(ScheduledDataImportServiceImpl.class);

    @Override
    public YukonJob scheduleDataImport(ScheduledDataImport data, YukonUserContext userContext,
            HttpServletRequest request) {
        log.info("Scheduling Data Import job. name=" + data.getScheduleName() + " importType=" + data.getImportType());
        ScheduledDataImportTask task = buildTask(scheduledDataImportJobDefinition, data);
        YukonJob job =
            jobManager.scheduleJob(scheduledDataImportJobDefinition, task, data.getCronString(), userContext);
        log.info("Task=" + task);
        log.info("Job scheduled=" + job);
        return job;
    }

    @Override
    public YukonJob updateDataImport(ScheduledDataImport data, YukonUserContext userContext,
            HttpServletRequest request) {

        log.info("Replacing Data Import job. existingJobId=" + data.getJobId() + " name=" + data.getScheduleName()
            + " importType=" + data.getImportType().getImportType());
        ScheduledDataImportTask task = buildTask(scheduledDataImportJobDefinition, data);
        YukonJob job = jobManager.replaceScheduledJob(data.getJobId(), scheduledDataImportJobDefinition, task,
            data.getCronString(), userContext);
        log.info("Task=" + task);
        log.info("Job scheduled=" + job);
        return job;
    }

    @Override
    public ScheduledDataImport getJobById(int jobId) {
        ScheduledRepeatingJob job = jobManager.getRepeatingJob(jobId);
        ScheduledDataImportTask task = (ScheduledDataImportTask) jobManager.instantiateTask(job);
        return getScheduleImportData(job, task);
    }

    @Override
    public ScheduledDataImport deleteJobById(int jobId) {
        ScheduledRepeatingJob job = jobManager.getRepeatingJob(jobId);
        ScheduledDataImportTask task = (ScheduledDataImportTask) jobManager.instantiateTask(job);
        log.info("Deleting Data Import job. jobId=" + job.getId() + "name=" + task.getScheduleName());
        jobManager.deleteJob(job);
        return getScheduleImportData(job, task);
    }

    @Override
    public JobState getJobState(int jobId) {
        JobStatus<YukonJob> status = jobStatusDao.findLatestStatusByJobId(jobId);
        JobDisabledStatus jobDisabledStatus = jobManager.getJobDisabledStatus(jobId);
        JobState jobState = JobState.of(jobDisabledStatus, status);
        return jobState;
    }

    /**
     * Builds the task with the specified jobDefinition and scheduledDataImport object.
     */
    private ScheduledDataImportTask buildTask(YukonJobDefinition<? extends ScheduledDataImportTask> jobDefinition,
            ScheduledDataImport data) {
        ScheduledDataImportTask task = jobDefinition.createBean();
        task.setScheduleName(data.getScheduleName());
        task.setImportPath(data.getImportPath());
        task.setErrorFileOutputPath(data.getErrorFileOutputPath());
        task.setImportType(data.getImportType().getDatabaseRepresentation());
        return task;
    }

    /**
     * Populates ScheduledDataImport fields using job and task.
     */
    private ScheduledDataImport getScheduleImportData(ScheduledRepeatingJob job, ScheduledDataImportTask task) {
        ScheduledDataImport scheduledDataImport = new ScheduledDataImport();
        scheduledDataImport.setJobId(job.getId());
        scheduledDataImport.setScheduleName(task.getScheduleName());
        scheduledDataImport.setImportPath(task.getImportPath());
        scheduledDataImport.setCronString(job.getCronString());
        scheduledDataImport.setErrorFileOutputPath(task.getErrorFileOutputPath());
        scheduledDataImport.setScheduleDescription(getScheduleDescription(job));
        scheduledDataImport.setImportType(ImportType.fromName(task.getImportType()));
        scheduledDataImport.setJobState(getJobState(job.getId()));
        return scheduledDataImport;
    }

    /**
     * Return schedule description with the specified job.
     */
    private String getScheduleDescription(ScheduledRepeatingJob job) {
        return cronExpressionTagService.getDescription(job.getCronString(), job.getUserContext());
    }

}
