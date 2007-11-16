package com.cannontech.jobs.dao;

import com.cannontech.jobs.model.YukonJob;

public interface YukonJobDao {
    public YukonJob getById(int id);
    public void update(YukonJob job);
}
