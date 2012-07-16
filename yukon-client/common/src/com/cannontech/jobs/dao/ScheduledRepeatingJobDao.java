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
    public Set<ScheduledRepeatingJob> getJobsByDefinition(YukonJobDefinition<? extends YukonTask> definition);
    public SeparableRowMapper<ScheduledRepeatingJob> getJobRowMapper();
    
    /**
     * Inserts a new job and its properties. When it completes, its id
     * will be set to the correct value to match what was set in the database.
     * @param repeatingJob
     */
    public void save(ScheduledRepeatingJob repeatingJob);
    
    public void update(ScheduledRepeatingJob repeatingJob);
}
