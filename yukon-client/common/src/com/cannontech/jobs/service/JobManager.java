package com.cannontech.jobs.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledOneTimeJob;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.support.ScheduleException;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.user.YukonUserContext;


public interface JobManager {

    public static final String JOB_MANAGER_DISABLED_KEY = "JOB_MANAGER_DISABLED";
    
    /** Schedule job helps in creating one-time schedule jobs.
     * @param jobDefinition - contains job definition parameters values
     * @param task - contains the task properties for job
     * @param time - start time of job to be scheduled
     * @param userContext - user details
     * @returns Yukon Job
     */
    public YukonJob scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, Date time,
            YukonUserContext userContext);

    /** Schedule job helps in creating repeating schedule job.
     * @param jobDefinition - contains job definition parameters values
     * @param task - contains the task properties for job
     * @param cronExpression - contains details about the repeating job in cron expression format
     * @param userContext - user details
     * @returns Yukon Job
     */
    public YukonJob scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, String cronExpression,
            YukonUserContext userContext);

    /** Schedule job helps in creating repeating schedule job, when job properties are passed.
     * @param jobDefinition - contains job definition parameters values
     * @param task - contains the task properties for job
     * @param cronExpression - contains details about the repeating job in cron expression format
     * @param userContext - user details
     * @param jobProperties - contains Job properties given by the user
     * @param jobGroupId
     * @return Yukon Job
     */
    public YukonJob scheduleJob(YukonJobDefinition<?> jobDefinition, YukonTask task, String cronExpression,
            YukonUserContext userContext, Map<String, String> jobProperties, int jobGroupId);

    /** Replaces the old repeating scheduled job with the new one.
     * @param jobId - Identifies the old job by primary key
     * @param jobDefinition - contains job definition parameters values
     * @param task - contains the task properties for job
     * @param cronExpression - contains details about the repeating job in cron expression format
     * @return Yukon Job
     */
    public YukonJob replaceScheduledJob(int jobId, YukonJobDefinition<?> jobDefinition, YukonTask task,
            String cronExpression);

    /** Replaces the old repeating scheduled job with the new one, when usercontext is given
     * @param jobId - Identifies the old job by primary key
     * @param jobDefinition - contains job definition parameters values
     * @param task - contains the task properties for job
     * @param cronExpression - contains details about the repeating job in cron expression format
     * @param userContext - user details
     * @return  Yukon Job
     */
    public YukonJob replaceScheduledJob(int jobId, YukonJobDefinition<?> jobDefinition, YukonTask task,
            String cronExpression, YukonUserContext userContext);

    /** Replaces the old repeating scheduled job with the new one, when usercontext and job properties are given
     * @param jobId - Identifies the old job by primary key
     * @param jobDefinition - contains job definition parameters values
     * @param task - contains the task properties for job
     * @param cronExpression - contains details about the repeating job in cron expression format
     * @param userContext - user details
     * @param jobProperties - contains Job properties given by the user
     * @return  Yukon Job
     */
    public YukonJob replaceScheduledJob(int jobId, YukonJobDefinition<?> jobDefinition, YukonTask task,
            String cronExpression, YukonUserContext userContext, Map<String, String> jobProperties);

    /** Searches the job by Job object given and enables the job
     */
    public void enableJob(YukonJob job);
    
    /** Searches the job by Job object given and disables job
     */
    public void disableJob(YukonJob job);
    
    /** Searches the job by Job object given and deletes the job
     */
    public void deleteJob(YukonJob job);
    
    /** Searches the job by Job object given and aborts the currently running job
     * @returns success flag
     */
    public boolean abortJob(YukonJob job);
    
    /** Gets the currently executing job list and returns the same list
     */
    public Collection<YukonJob> getCurrentlyExecuting();
    
    /** Initializes the job with the properties and returns the YukonTask instantiated
     */
    public YukonTask instantiateTask(YukonJob job);
    
    /** Searches job by jobId and returns the instance of YukonJob 
     */
    public YukonJob getJob(int jobId);
    
    /** Searches for job by JobId and returns the job Disabled status
     */
    public JobDisabledStatus getJobDisabledStatus(int jobId);
    
    /** Searches for job by jobId and returns the instance of ScheduledRepeatingJob
     */
    public ScheduledRepeatingJob getRepeatingJob(int jobId);
    
    /** Searches for un-run one-time jobs with common job definition given and returns the set of ScheduledOneTimeJob
     */
    public Set<ScheduledOneTimeJob> getUnRunOneTimeJobsByDefinition(YukonJobDefinition<? extends YukonTask> definition);

    /** Searches for un-run repeating jobs with common job definition given and returns the set of ScheduledRepeatingJob
     */
    public Set<ScheduledRepeatingJob> getUnRunRepeatingJobsByDefinition(
            YukonJobDefinition<? extends YukonTask> definition);

    /**
     * Searches for non-deleted repeating jobs with common job definition given and returns the set of
     * ScheduledRepeatingJob
     */
    public List<ScheduledRepeatingJob> getNotDeletedRepeatingJobsByDefinition(
            YukonJobDefinition<? extends YukonTask> definition);

    /** Calculates and returns the next valid run-time date for the given ScheduledRepeatingJob which comes 
     * after the date specified.
     * @throws ScheduleException - Exception is thrown in case the calculation fails
     */
    public Date getNextRuntime(ScheduledRepeatingJob job, Date from) throws ScheduleException;

    /**
     * Starts Job
     * 
     * @param newCronString - if the newCronString is null the job will be started right away otherwise the job will be
     *        updated with a new cron string and scheduled to start.
     * @throws ScheduleException 
     */
    void startJob(ScheduledRepeatingJob job, String newCronString) throws ScheduleException;

    /**
     * This method sets a cron string for scheduled job to never run ({@link ScheduledRepeatingJob#NEVER_RUN_CRON_STRING}).
     */
    void unscheduleJob(ScheduledRepeatingJob job);
    
    /**
     * Gets most recent JobStatus for the job belonging to the job group of the given jobId.
     */
    public JobStatus<YukonJob> getLatestStatusByJobId(int jobId);

    /**
     * Toggles Job status. If disabled, make enabled. If enabled,
     * make disabled. Returns new state of the job.
     */
    public boolean toggleJobStatus(YukonJob job);

}