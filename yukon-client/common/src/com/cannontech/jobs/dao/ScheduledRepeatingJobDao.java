package com.cannontech.jobs.dao;

import java.util.Set;

import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledRepeatingJob;

public interface ScheduledRepeatingJobDao {
    
    public ScheduledRepeatingJob getById(int id);
    public Set<ScheduledRepeatingJob> getAll();
    public Set<JobStatus<ScheduledRepeatingJob>> getAllUnfinished();
    
    /**
     * Saves the job and its properties. When it completes, its id
     * will be set to the correct value to match what was set in the database.
     * @param repeatingJob
     */
    public void save(ScheduledRepeatingJob repeatingJob);

}
