package com.cannontech.jobs.dao;

import java.util.Set;

import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledOneTimeJob;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;

public interface ScheduledOneTimeJobDao {
    
    public ScheduledOneTimeJob getById(int id);
    public Set<ScheduledOneTimeJob> getAll();
    public Set<JobStatus<ScheduledOneTimeJob>> getAllUnfinished();
    public Set<ScheduledOneTimeJob> getJobsByDefinition(YukonJobDefinition<? extends YukonTask> definition);
    
    /**
     * Saves the job and its properties. When it completes, its id
     * will be set to the correct value to match what was set in the database.
     * @param oneTimeJob
     */
    public void save(ScheduledOneTimeJob oneTimeJob);
    
    public Set<ScheduledOneTimeJob> getAllUnstarted();

    public JobDisabledStatus getJobDisabledStatusById(int jobId);
}
