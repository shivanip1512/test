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
     * @return
     */
    public List<JobStatus<YukonJob>> getAllStatus(Date start, Date end);
    
    /**
     * Get JobStatus for givin jobId. 
     * 
     * NOTE: Assumes valid jobId. If no status is found for jobId, assumption
     * will be made that the job has not run yet.
     * 
     * @param jobId
     * @return
     */
    public JobStatus<YukonJob> getStatusByJobId(int jobId);

}
