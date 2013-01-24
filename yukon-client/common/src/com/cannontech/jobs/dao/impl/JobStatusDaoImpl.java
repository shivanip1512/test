package com.cannontech.jobs.dao.impl;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.DateRowMapper;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.YukonJob;

public class JobStatusDaoImpl implements JobStatusDao, InitializingBean {
    protected YukonJdbcTemplate yukonJdbcTemplate;
    protected NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<JobStatus<?>> template;
    private YukonJobBaseRowMapper yukonJobBaseRowMapper;

    private final FieldMapper<JobStatus<?>> jobStatusFieldMapper = new FieldMapper<JobStatus<?>>() {

        public void extractValues(MapSqlParameterSource p, JobStatus<?> status) {
            int jobId = status.getJob().getId();
            p.addValue("jobId", jobId);
            Date startTime = status.getStartTime();
            p.addValue("startTime", startTime, Types.TIMESTAMP);
            Date stopTime = status.getStopTime();
            p.addValue("stopTime", stopTime, Types.TIMESTAMP);
            String jobState = status.getJobState().name();
            p.addValue("jobState", jobState);
            String message = status.getMessage();
            p.addValue("message", message);   
        }

        public Number getPrimaryKey(JobStatus<?> status) {
            return status.getId();
        }

        public void setPrimaryKey(JobStatus<?> status, int value) {
            status.setId(value);
        }
    };
    
    public void saveOrUpdate(JobStatus<?> status) {
        template.save(status);
    }
    
    public List<JobStatus<YukonJob>> getAllStatus(Date start, Date end) {
        Date earlyLimit;
        Date lateLimit;
        boolean reverse;
        if (start.before(end)) {
            earlyLimit = start;
            lateLimit = end;
            reverse = false;
        } else {
            earlyLimit = end;
            lateLimit = start;
            reverse = true;
        }
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from JobStatus js");
        sql.append("join Job j on js.jobid = j.jobid");
        sql.append("where startTime").lt(lateLimit);
        sql.append("and stopTime").gt(earlyLimit);
        sql.append("order by startTime " + (reverse ? "desc" : ""));
        JobStatusRowMapper<YukonJob> mapper = new JobStatusRowMapper<YukonJob>(yukonJobBaseRowMapper);
        
        List<JobStatus<YukonJob>> resultList = yukonJdbcTemplate.query(sql, mapper);
        return resultList;
    }
    
    public JobStatus<YukonJob> findLatestStatusByJobId(int jobId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from JobStatus js");
        sql.append("join Job j on js.jobid = j.jobid");
        sql.append("where js.jobid").eq(jobId);
        sql.append("order by js.startTime desc");
        
        JobStatusRowMapper<YukonJob> mapper = new JobStatusRowMapper<YukonJob>(yukonJobBaseRowMapper);
        List<JobStatus<YukonJob>> results = yukonJdbcTemplate.query(sql, mapper);
        
        if (results.size() == 0) {
        	return null;
        }
        
        return results.get(0);
    }
    
    public Date findJobLastSuccessfulRunDate(int jobId) {
        
        Date result = null;
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT MAX(js.StartTime) AS lastOkRun");
            sql.append("FROM JobStatus js");
            sql.append("JOIN Job j ON js.jobid = j.jobid");
            sql.append("WHERE js.jobid = ?");
            sql.append("AND js.JobState = ?");
            result = yukonJdbcTemplate.queryForObject(sql.toString(), Date.class, jobId, 
                                                      JobState.COMPLETED.name());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return result;
    }
    
    public Date findJobLastCompletedRunDate(int jobId) {
    	Date result = null;
    	try {
    		SqlStatementBuilder sql = new SqlStatementBuilder();
    		sql.append("SELECT MAX(js.StartTime) AS lastOkRun");
            sql.append("FROM JobStatus js");
            sql.append("JOIN Job j ON js.jobid = j.jobid");
            sql.append("WHERE js.jobId").eq(jobId);
            sql.append("AND js.JobState").in(JobState.getCompletedJobStateNames());
            result = yukonJdbcTemplate.queryForObject(sql, new DateRowMapper());
    	} catch (EmptyResultDataAccessException e) {
    		return null;
    	}
    	return result;
    }
    
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<JobStatus<?>>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("JobStatus");
        template.setPrimaryKeyField("jobStatusId");
        template.setFieldMapper(jobStatusFieldMapper); 
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    @Required
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Required
    public void setYukonJobMapper(YukonJobBaseRowMapper yukonJobBaseRowMapper) {
        this.yukonJobBaseRowMapper = yukonJobBaseRowMapper;
    }

}
