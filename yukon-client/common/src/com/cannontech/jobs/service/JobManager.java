package com.cannontech.jobs.service;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import com.cannontech.jobs.model.ScheduledOneTimeJob;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.user.YukonUserContext;


public interface JobManager {

    public void scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, Date time);
    public void scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, Date time, YukonUserContext userContext);
    public void scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, String cronExpression);
    public void scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, String cronExpression, YukonUserContext userContext);
    
    public void enableJob(YukonJob job);
    public void disableJob(YukonJob job);
    public boolean abortJob(YukonJob job);
    public Collection<YukonJob> getCurrentlyExecuting();
    public YukonTask instantiateTask(YukonJob job);
    
    public YukonJob getJob(int jobId);
    
    public Set<ScheduledOneTimeJob> getUnRunOneTimeJobsByDefinition(YukonJobDefinition<? extends YukonTask> definition);
    public Set<ScheduledRepeatingJob> getUnRunRepeatingJobsByDefinition(YukonJobDefinition<? extends YukonTask> definition);

}