package com.cannontech.jobs.dao.impl;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.jobs.dao.YukonJobDao;
import com.cannontech.jobs.model.YukonJob;

public class YukonJobDaoImpl extends JobDaoBase implements YukonJobDao {

    private YukonJobBaseRowMapper yukonJobBaseRowMapper;

    @Override
    public YukonJob getById(int id) {
        String sql =
            "select * " + 
            "from Job j " + 
            "where j.jobId = ?";

        YukonJob job = jdbcTemplate.queryForObject(sql, yukonJobBaseRowMapper, id);

        return job;
    }

    @Override
    public void update(YukonJob job) {
        updateJob(job);
    }
    
    @Required
    public void setYukonJobMapper(YukonJobBaseRowMapper yukonJobMapper) {
        this.yukonJobBaseRowMapper = yukonJobMapper;
    }


}
