package com.cannontech.jobs.dao;

import java.util.Set;

import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.spring.SeparableRowMapper;

public interface ScheduledRepeatingJobDao {
    
    public ScheduledRepeatingJob getById(int id);
    public Set<ScheduledRepeatingJob> getAll();
    public Set<JobStatus<ScheduledRepeatingJob>> getAllUnfinished();
    /**
     * Returns set of (non-deleted) scheduled jobs.
     */
    public Set<ScheduledRepeatingJob> getJobsByDefinition(YukonJobDefinition<? extends YukonTask> definition);
    
    /**
     * Returns set of scheduled jobs that are considered runnable yet
     * These include enabled jobs, with either no JobStatus or JobState=Restarted
     */
    public Set<ScheduledRepeatingJob> getJobsStillRunnableByDefinition(YukonJobDefinition<? extends YukonTask> definition);
    
    public SeparableRowMapper<ScheduledRepeatingJob> getJobRowMapper();
    
    /**
     * Inserts a new job and its properties. When it completes, its id
     * will be set to the correct value to match what was set in the database.
     * @param repeatingJob
     */
    public void save(ScheduledRepeatingJob repeatingJob);
    
    public void update(ScheduledRepeatingJob repeatingJob);
}
