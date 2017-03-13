package com.cannontech.jobs.dao;

import java.util.Date;
import java.util.List;

import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.YukonJob;

public interface JobStatusDao {

    public void saveOrUpdate(JobStatus<?> status);
    
    /**
     * Result will be ordered from start to end, so list may be in increasing
     * or decreasing order based on the relative values given for start and end.
     * @param start - inclusive
     * @param end - exclusive
     */
    public List<JobStatus<YukonJob>> getAllStatus(Date start, Date end);
    
    /**
     * Get most recent JobStatus for givin jobId. 
     * 
     * NOTE: Assumes valid jobId. If no status is found for jobId, assumption
     * will be made that the job has not run yet.
     */
    public JobStatus<YukonJob> findLatestStatusByJobId(int jobId);
    
    /**
     * Gets the most recent StartTime for given job whose status is 'COMPLETED'
     */
    public Date findJobLastSuccessfulRunDate(int jobId);
    
    /**
     * Gets the most recent StartTime of the job belonging to job group of the given job id.  
     */
    public Date findLastRunDateForJobGroup(int jobId);
    
    /**
     * Gets the job id of the latest executed or executing job belonging to the job group of the given job id.
     */
    public Integer findLastestJobInJobGroup(int jobId);
}
