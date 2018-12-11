package com.cannontech.web.scheduledDataImport.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.scheduledFileImport.ScheduledImportType;
import com.cannontech.common.scheduledFileImport.ScheduledDataImport;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.common.scheduledDataImportTask.ScheduledDataImportTaskJobWrapperFactory;
import com.cannontech.web.common.scheduledDataImportTask.ScheduledDataImportTaskJobWrapperFactory.ScheduledDataImportTaskJobWrapper;
import com.cannontech.web.scheduledDataImport.service.ScheduledDataImportService;
import com.cannontech.web.scheduledDataImport.tasks.ScheduledDataImportTask;
import com.cannontech.web.stars.scheduledDataImport.ScheduledDataImportController.Column;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;

public class ScheduledDataImportServiceImpl implements ScheduledDataImportService {

    @Autowired private JobManager jobManager;
    @Autowired @Qualifier("scheduledDataImport") 
    private YukonJobDefinition<ScheduledDataImportTask> scheduledDataImportJobDefinition;
    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private JobStatusDao jobStatusDao;
    @Autowired private ScheduledDataImportTaskJobWrapperFactory scheduledDataImportTaskJobWrapperFactory;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
    
    private Map<Column, Comparator<ScheduledDataImportTaskJobWrapper>> sorters;
    @PostConstruct
    public void initialize() {
        Builder<Column, Comparator<ScheduledDataImportTaskJobWrapper>> builder = ImmutableMap.builder();
        builder.put(Column.NAME, ScheduledDataImportTaskJobWrapperFactory.getJobNameComparator());
        builder.put(Column.TYPE, ScheduledDataImportTaskJobWrapperFactory.getJobTypeComparator());
        builder.put(Column.SCHEDULE, ScheduledDataImportTaskJobWrapperFactory.getRunScheduleComparator());
        builder.put(Column.NEXT_RUN, ScheduledDataImportTaskJobWrapperFactory.getNextRunComparator());
        builder.put(Column.STATUS, ScheduledDataImportTaskJobWrapperFactory.getStatusComparator());
        sorters = builder.build();
    }

    private Logger log = YukonLogManager.getLogger(ScheduledDataImportServiceImpl.class);
    private static final String baseKey = "yukon.web.modules.operator.scheduledDataImportList.";

    @Override
    public YukonJob scheduleDataImport(ScheduledDataImport data, YukonUserContext userContext) {
        log.info("Scheduling Data Import job. name=" + data.getScheduleName() + " importType=" + data.getImportType());
        ScheduledDataImportTask task = buildTask(scheduledDataImportJobDefinition, data);
        YukonJob job =
            jobManager.scheduleJob(scheduledDataImportJobDefinition, task, data.getCronString(), userContext);
        log.info("Task=" + task);
        log.info("Job scheduled=" + job);
        return job;
    }

    @Override
    public YukonJob updateDataImport(ScheduledDataImport data, YukonUserContext userContext) {

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
        scheduledDataImport.setImportType(ScheduledImportType.fromName(task.getImportType()));
        scheduledDataImport.setJobState(getJobState(job.getId()));
        return scheduledDataImport;
    }

    /**
     * Return schedule description with the specified job.
     */
    private String getScheduleDescription(ScheduledRepeatingJob job) {
        return cronExpressionTagService.getDescription(job.getCronString(), job.getUserContext());
    }

    @Override
    public SearchResults<ScheduledDataImportTaskJobWrapper> getScheduledFileImportJobData(PagingParameters paging, SortingParameters sorting,YukonUserContext userContext) {
        List<ScheduledRepeatingJob> exportJobs =
            jobManager.getNotDeletedRepeatingJobsByDefinition(scheduledDataImportJobDefinition);
        List<ScheduledDataImportTaskJobWrapper> jobWrappers = Lists.newArrayListWithCapacity(exportJobs.size());
        for (ScheduledRepeatingJob job : exportJobs) {
            ScheduledDataImportTaskJobWrapper jobWrapper =
                scheduledDataImportTaskJobWrapperFactory.createJobWrapper(job, userContext);
            jobWrappers.add(jobWrapper);
        }
        Direction dir = sorting.getDirection();
        Column sortBy = Column.valueOf(sorting.getSort());
        Comparator<ScheduledDataImportTaskJobWrapper> comparator = sorters.get(sortBy);
        if (dir == Direction.desc) {
            Collections.sort(jobWrappers, Collections.reverseOrder(comparator));
        } else {
            Collections.sort(jobWrappers, comparator);
        }
        return SearchResults.pageBasedForWholeList(paging, jobWrappers);
    }

    @Override
    public boolean toggleEnabled(int jobId, LiteYukonUser yukonUser) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.MANAGE_SCHEDULES, yukonUser);
        YukonJob job = jobManager.getJob(jobId);
        boolean enabled = false;
        if (job.isDisabled()) {
            jobManager.enableJob(job);
            enabled = true;
        } else {
            jobManager.disableJob(job);
        }
        return enabled;
    }

    @Override
    public Map<String, Object> startJob(String jobId, HttpServletRequest request) {
        Map<String, Object> json = new HashMap<>();
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);
        int toggleJobId = Integer.parseInt(jobId);
        if (jobManager.getJob(toggleJobId).isDeleted()) {
            json.put("error", messageSourceAccessor.getMessage(baseKey + "error.editDeletedJob"));
            return json;
        }
        String cronExpression = null;
        try {
            cronExpression = cronExpressionTagService.build(jobId, request, userContext);
        } catch (Exception e) {}
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(toggleJobId);
        try {
            jobManager.startJob(job, cronExpression);
        } catch (ScheduleException e) {
            json.put("error", messageSourceAccessor.getMessage(baseKey + "error.pastDate"));
        }
        return json;
    }
}
