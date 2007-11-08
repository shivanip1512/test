package com.cannontech.jobs.dao;

import com.cannontech.jobs.model.JobStatus;

public interface JobStatusDao {

    void saveOrUpdate(JobStatus<?> status);

}
