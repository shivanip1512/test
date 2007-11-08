package com.cannontech.jobs.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.spring.SeparableRowMapper;

public class ScheduledRepeatingJobDaoImpl extends JobDaoBase implements ScheduledRepeatingJobDao {
    private FieldMapper<ScheduledRepeatingJob> jobFieldMapper = new FieldMapper<ScheduledRepeatingJob>() {
        public void extractValues(MapSqlParameterSource p, ScheduledRepeatingJob job) {
            String cronString = job.getCronString();
            p.addValue("cronString", cronString);
            
        }
        public Number getPrimaryKey(ScheduledRepeatingJob job) {
            return job.getId();
        }
        public void setPrimaryKey(ScheduledRepeatingJob job, int value) {
            job.setId(value);
        }
    };
    
    private SeparableRowMapper<ScheduledRepeatingJob> jobRowMapper = 
        new SeparableRowMapper<ScheduledRepeatingJob>(yukonJobMapper) {
        protected ScheduledRepeatingJob createObject(ResultSet rs) throws SQLException {
            ScheduledRepeatingJob job = new ScheduledRepeatingJob();
            return job;
        }
        protected void mapRow(ResultSet rs, ScheduledRepeatingJob job) throws SQLException {
            job.setCronString(rs.getString("cronString"));
        }
    };

    private ParameterizedRowMapper<JobStatus<ScheduledRepeatingJob>> jobStatusRowMapper = 
        new JobStatusRowMapper<ScheduledRepeatingJob>(jobRowMapper);
    
    private SimpleTableAccessTemplate<ScheduledRepeatingJob> template;
    
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        template = new SimpleTableAccessTemplate<ScheduledRepeatingJob>(jdbcTemplate, nextValueHelper);
        template.withTableName("JobScheduledRepeating");
        template.withPrimaryKeyField("jobId");
        template.withFieldMapper(jobFieldMapper); 
    }

    public Set<ScheduledRepeatingJob> getAll() {
        String sql = 
            "select * " +
            "from JobScheduledRepeating jsr " +
            "join Job on Job.jobId = jsr.jobId";
        
        List<ScheduledRepeatingJob> jobList = jdbcTemplate.query(sql, jobRowMapper);
        Set<ScheduledRepeatingJob> jobSet = new HashSet<ScheduledRepeatingJob>(jobList);
        
        return jobSet;
    }

    public Set<JobStatus<ScheduledRepeatingJob>> getAllUnfinished() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from JobStatus js");
        sql.append("join JobScheduledRepeating jsr on js.jobId = jsr.jobId");
        sql.append("join Job j on jsr.jobId = j.jobId");
        sql.append("where jobState = ?");
        String jobState = JobState.STARTED.name();
        List<JobStatus<ScheduledRepeatingJob>> resultList = jdbcTemplate.query(sql.toString(), jobStatusRowMapper, jobState);
        HashSet<JobStatus<ScheduledRepeatingJob>> resultSet = new HashSet<JobStatus<ScheduledRepeatingJob>>(resultList);
        return resultSet;
    }

    public ScheduledRepeatingJob getById(int id) {
        return null;
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    public void save(ScheduledRepeatingJob repeatingJob) {
        try {
            // create the Job entry first
            saveJob(repeatingJob);
            // create JobScheduledRepeating entry
            template.save(repeatingJob);

        } catch (RuntimeException e) {
            // if an exception gets thrown, the transaction will be rolled back
            // in this case we want to reset the id to null
            repeatingJob.setId(null);
            throw e;
        }        
    }

}
