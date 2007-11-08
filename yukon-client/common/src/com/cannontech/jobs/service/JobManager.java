package com.cannontech.jobs.service;

import java.util.Date;
import java.util.SortedSet;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;


public interface JobManager {

    public interface ScheduledJobInfo extends Comparable<ScheduledJobInfo> {
        public YukonJob getJob();
        public Date getTime();
        boolean isRunning();
    }

    public void scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, Date time);
    public void scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, Date time, LiteYukonUser runAs);
    public void scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, String cronExpression);
    public void scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, String cronExpression, LiteYukonUser runAs);

    public SortedSet<ScheduledJobInfo> getScheduledJobInfo();

}