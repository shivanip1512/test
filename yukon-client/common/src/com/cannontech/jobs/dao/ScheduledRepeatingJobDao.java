package com.cannontech.jobs.dao;

import java.util.Set;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;

public interface ScheduledRepeatingJobDao {
    
    public ScheduledRepeatingJob getById(int id);
    public Set<ScheduledRepeatingJob> getAll();
    public Set<JobStatus<ScheduledRepeatingJob>> getAllUnfinished();
    public Set<ScheduledRepeatingJob> getJobsByDefinition(YukonJobDefinition<? extends YukonTask> definition);
    public ParameterizedRowMapper<ScheduledRepeatingJob> getJobRowMapper();
    
    /**
     * Inserts a new job and its properties. When it completes, its id
     * will be set to the correct value to match what was set in the database.
     * @param repeatingJob
     */
    public void save(ScheduledRepeatingJob repeatingJob);
    
    public void update(ScheduledRepeatingJob repeatingJob);

    public JobDisabledStatus getJobDisabledStatusById(int jobId);
}
