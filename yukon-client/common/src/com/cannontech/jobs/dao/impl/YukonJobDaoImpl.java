package com.cannontech.jobs.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.jobs.dao.ScheduledOneTimeJobDao;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.dao.YukonJobDao;
import com.cannontech.jobs.model.YukonJob;

public class YukonJobDaoImpl extends JobDaoBase implements YukonJobDao {
    @Autowired private ScheduledOneTimeJobDao scheduledOneTimeJobDao;
    @Autowired private ScheduledRepeatingJobDao scheduledRepeatingJobDao;

    @Override
    public  YukonJob getById(int id) {
        YukonJob job = null;
        
        // the job must be either one time or recurring (otherwise you have bigger problems..)
        // so try looking for it in JobScheduledOneTime first
        // if error, try looking for it in JobScheduledRepeating then..
        // this will tell us what which table we are goinf to join job on and what row mapper to use so that we can 
        // return a specific type of YukonJob (ScheduledOneTimeJob or ScheduledRepeatingJob).
        try {
            job = scheduledOneTimeJobDao.getById(id);
        } catch (EmptyResultDataAccessException e1) {
            try {
                job = scheduledRepeatingJobDao.getById(id);
            } catch (EmptyResultDataAccessException e2) {
                throw new NotFoundException("Unknown job id " + id); 
            }
        }
        
        return job;
    }
    
    @Override
    public JobDisabledStatus getJobDisabledStatusById(int jobId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Disabled");
        sql.append("FROM Job");
        sql.append("WHERE jobId").eq(jobId);
        JobDisabledStatus result = jdbcTemplate.queryForObject(sql, jobDisabledStatusRowMapper);
        return result;
    }

    @Override
    public void update(YukonJob job) {
        updateJob(job);
    }
}
