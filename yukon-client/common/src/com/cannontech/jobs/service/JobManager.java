package com.cannontech.jobs.service;

import java.util.Collection;
import java.util.Date;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;


public interface JobManager {

    public void scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, Date time);
    public void scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, Date time, LiteYukonUser runAs);
    public void scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, String cronExpression);
    public void scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, String cronExpression, LiteYukonUser runAs);
    
    public void enableJob(YukonJob job);
    public void disableJob(YukonJob job);
    public boolean abortJob(YukonJob job);
    public Collection<YukonJob> getCurrentlyExecuting();

}