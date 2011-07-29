package com.cannontech.jobs.dao;

import com.cannontech.jobs.dao.impl.JobDisabledStatus;
import com.cannontech.jobs.model.YukonJob;

public interface YukonJobDao {
    public YukonJob getById(int id);
    public JobDisabledStatus getJobDisabledStatusById(int jobId);
    public void update(YukonJob job);
}
