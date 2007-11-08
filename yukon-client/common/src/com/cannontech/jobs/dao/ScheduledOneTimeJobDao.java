package com.cannontech.jobs.dao;

import java.util.Set;

import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledOneTimeJob;

public interface ScheduledOneTimeJobDao {
    
    public ScheduledOneTimeJob getById(int id);
    public Set<ScheduledOneTimeJob> getAll();
    public Set<JobStatus<ScheduledOneTimeJob>> getAllUnfinished();
    
    /**
     * Saves the job and its properties. When it completes, its id
     * will be set to the correct value to match what was set in the database.
     * @param repeatingJob
     */
    public void save(ScheduledOneTimeJob repeatingJob);

}
