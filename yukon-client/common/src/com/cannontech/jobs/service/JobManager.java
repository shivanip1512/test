package com.cannontech.jobs.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.ScheduledOneTimeJob;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.user.YukonUserContext;


public interface JobManager {

    public static final String JOB_MANAGER_DISABLED_KEY = "JOB_MANAGER_DISABLED";
    
    public YukonJob scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, Date time);
    public YukonJob scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, Date time, YukonUserContext userContext);
    public YukonJob scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, String cronExpression);
    public YukonJob scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, String cronExpression, YukonUserContext userContext);
    public YukonJob replaceScheduledJob(int jobId, YukonJobDefinition<?> jobDefinition, YukonTask task, String cronExpression, YukonUserContext userContext);
    
    public void enableJob(YukonJob job);
    public void disableJob(YukonJob job);
    public void deleteJob(YukonJob job);
    public boolean abortJob(YukonJob job);
    public Collection<YukonJob> getCurrentlyExecuting();
    public YukonTask instantiateTask(YukonJob job);
    
    public YukonJob getJob(int jobId);
    public JobDisabledStatus getJobDisabledStatus(int jobId);
    public ScheduledRepeatingJob getRepeatingJob(int jobId);
    
    public Set<ScheduledOneTimeJob> getUnRunOneTimeJobsByDefinition(YukonJobDefinition<? extends YukonTask> definition);
    public Set<ScheduledRepeatingJob> getUnRunRepeatingJobsByDefinition(YukonJobDefinition<? extends YukonTask> definition);
    public List<ScheduledRepeatingJob> getNotDeletedRepeatingJobsByDefinition(YukonJobDefinition<? extends YukonTask> definition);
    
    public Date getNextRuntime(ScheduledRepeatingJob job, Date from) throws ScheduleException;

}