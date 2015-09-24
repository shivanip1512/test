package com.cannontech.jobs.dao.impl;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.DateRowMapper;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.model.JobRunStatus;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.YukonJob;

public class JobStatusDaoImpl implements JobStatusDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJobBaseRowMapper yukonJobBaseRowMapper;

    private SimpleTableAccessTemplate<JobStatus<?>> template;

    private final FieldMapper<JobStatus<?>> jobStatusFieldMapper = new FieldMapper<JobStatus<?>>() {
        @Override
        public void extractValues(MapSqlParameterSource p, JobStatus<?> status) {
            int jobId = status.getJob().getId();
            p.addValue("jobId", jobId);
            Date startTime = status.getStartTime();
            p.addValue("startTime", startTime, Types.TIMESTAMP);
            Date stopTime = status.getStopTime();
            p.addValue("stopTime", stopTime, Types.TIMESTAMP);
            String jobState = status.getJobRunStatus().name();
            p.addValue("jobState", jobState);
            String message = status.getMessage();
            p.addValue("message", message);   
        }

        @Override
        public Number getPrimaryKey(JobStatus<?> status) {
            return status.getId();
        }

        @Override
        public void setPrimaryKey(JobStatus<?> status, int value) {
            status.setId(value);
        }
    };
    
    @Override
    public void saveOrUpdate(JobStatus<?> status) {
        template.save(status);
    }
    
    @Override
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
        
        List<JobStatus<YukonJob>> resultList = jdbcTemplate.query(sql, mapper);
        return resultList;
    }
    
    @Override
    public JobStatus<YukonJob> findLatestStatusByJobId(int jobId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from JobStatus js");
        sql.append("join Job j on js.jobid = j.jobid");
        sql.append("where js.jobid").eq(jobId);
        sql.append("order by js.startTime desc");
        
        JobStatusRowMapper<YukonJob> mapper = new JobStatusRowMapper<YukonJob>(yukonJobBaseRowMapper);
        List<JobStatus<YukonJob>> results = jdbcTemplate.query(sql, mapper);
        
        if (results.size() == 0) {
        	return null;
        }
        
        return results.get(0);
    }
    
    @Override
    public Date findJobLastSuccessfulRunDate(int jobId) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT MAX(js.StartTime) AS lastOkRun");
            sql.append("FROM JobStatus js");
            sql.append("JOIN Job j ON js.jobid = j.jobid");
            sql.append("WHERE js.jobid").eq(jobId);
            sql.append("AND js.JobState").eq_k(JobRunStatus.COMPLETED);
            
            Instant result = jdbcTemplate.queryForObject(sql, RowMapper.INSTANT);
            return result.toDate();
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public Date findJobLastCompletedRunDate(int jobId) {
    	Date result = null;
    	try {
    		SqlStatementBuilder sql = new SqlStatementBuilder();
    		sql.append("SELECT MAX(js.StartTime) AS lastOkRun");
            sql.append("FROM JobStatus js");
            sql.append("JOIN Job j ON js.jobid = j.jobid");
            sql.append("WHERE js.jobId").eq(jobId);
            sql.append("AND js.JobState").in(JobRunStatus.getCompletedJobStateNames());
            result = jdbcTemplate.queryForObject(sql, new DateRowMapper());
    	} catch (EmptyResultDataAccessException e) {
    		return null;
    	}
    	return result;
    }
    
    @PostConstruct
    public void init() throws Exception {
        template = new SimpleTableAccessTemplate<JobStatus<?>>(jdbcTemplate, nextValueHelper);
        template.setTableName("JobStatus");
        template.setPrimaryKeyField("jobStatusId");
        template.setFieldMapper(jobStatusFieldMapper); 
    }
}
