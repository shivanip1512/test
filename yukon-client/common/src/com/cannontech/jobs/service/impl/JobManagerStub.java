package com.cannontech.jobs.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.jobs.model.ScheduledOneTimeJob;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.JobManagerException;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.user.YukonUserContext;

/**
 * This is a Stub implementation of JobManager, stubbed in web-servers that
 * shouldn't run the jobs.
 */
public class JobManagerStub implements JobManager {

    private Logger log = YukonLogManager.getLogger(JobManagerStub.class);
    private boolean initialized = false;    
    private static final String JOB_MANAGER_DISABLED_MSG = "!!! Job Manager is disabled by Configuration !!!";

    @Override
    public void initialize() {
        // Safety-net in case this initialize() gets called more than once 
        // Spring caches singletons created out of FactoryBean, but this check insulates
        // the bean from any changes to that.
        // initialize() doesn't do much here, but this trap is covered if this Stub
        // starts to implement some functionality.
        if (initialized) return;
        
        log.warn(JOB_MANAGER_DISABLED_MSG + " Job Manager won't run on this server !!!");

        // track that Job Manager Stub is initialized ok
        initialized = true;
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
    public void scheduleJob(YukonJobDefinition<?> jobDefinition,
            YukonTask task, Date time) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public void scheduleJob(YukonJobDefinition<?> jobDefinition,
            YukonTask task, Date time, YukonUserContext userContext) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public void scheduleJob(YukonJobDefinition<?> jobDefinition,
            YukonTask task, String cronExpression) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

    @Override
    public void scheduleJob(YukonJobDefinition<?> jobDefinition,
            YukonTask task, String cronExpression, YukonUserContext userContext) {
        throw new JobManagerException(JOB_MANAGER_DISABLED_MSG);
    }

}
