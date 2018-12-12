package com.cannontech.jobs.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledOneTimeJob;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.JobManagerException;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.user.YukonUserContext;

/**
 * This is a Stub implementation of JobManager, stubbed in web-servers that
 * shouldn't run the jobs.
 */
public class JobManagerStub implements JobManager {

    private Logger log = YukonLogManager.getLogger(JobManagerStub.class);
    private static final String JOB_MANAGER_DISABLED_MSG = "!!! Job Manager is disabled by Configuration !!!";

    public void initialize() {
        log.warn(JOB_MANAGER_DISABLED_MSG + " Job Manager won't run on this server !!!");
    }

    @Override
    public boolean abortJob(YukonJob job) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public void disableJob(YukonJob job) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }
    
    @Override
    public void deleteJob(YukonJob job) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public void enableJob(YukonJob job) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public Collection<YukonJob> getCurrentlyExecuting() {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public YukonJob getJob(int jobId) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public JobDisabledStatus getJobDisabledStatus(int jobId) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public ScheduledRepeatingJob getRepeatingJob(int jobId) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public Set<ScheduledOneTimeJob> getUnRunOneTimeJobsByDefinition(
            YukonJobDefinition<? extends YukonTask> definition) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public Set<ScheduledRepeatingJob> getUnRunRepeatingJobsByDefinition(
            YukonJobDefinition<? extends YukonTask> definition) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public YukonTask instantiateTask(YukonJob job) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public YukonJob scheduleJob(YukonJobDefinition<?> jobDefinition,
            YukonTask task, Date time, YukonUserContext userContext) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }
    
    @Override
    public YukonJob scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task,
                                String cronExpression, YukonUserContext userContext,
                                Map<String, String> jobProperties, int jobGroupId) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }
    
    @Override
    public YukonJob scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task,
                                String cronExpression, YukonUserContext userContext) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }
    
    @Override
    public YukonJob replaceScheduledJob(int jobId, YukonJobDefinition<?> jobDefinition,
                                        YukonTask task, String cronExpression) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }
    
    @Override
    public YukonJob replaceScheduledJob(int jobId, YukonJobDefinition<?> jobDefinition,
                                        YukonTask task, String cronExpression,
                                        YukonUserContext userContext,
                                        Map<String, String> jobProperties) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public Date getNextRuntime(ScheduledRepeatingJob job, Date from) throws ScheduleException {
    	throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public YukonJob replaceScheduledJob(int jobId, YukonJobDefinition<?> jobDefinition,
                                        YukonTask task, String cronExpression,
                                        YukonUserContext userContext) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public List<ScheduledRepeatingJob> getNotDeletedRepeatingJobsByDefinition(YukonJobDefinition<? extends YukonTask> definition) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public void startJob(ScheduledRepeatingJob job, String newCronString) throws ScheduleException {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public void unscheduleJob(ScheduledRepeatingJob job) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public JobStatus<YukonJob> getLatestStatusByJobId(int jobId) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public boolean toggleJobStatus(YukonJob job) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

}
