package com.cannontech.jobs.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.spring.SeparableRowMapper;

public class ScheduledRepeatingJobDaoImpl extends JobDaoBase implements ScheduledRepeatingJobDao, InitializingBean {
    private final FieldMapper<ScheduledRepeatingJob> jobFieldMapper = new FieldMapper<ScheduledRepeatingJob>() {
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
    
    private YukonJobBaseRowMapper yukonJobBaseRowMapper;
    
    private SeparableRowMapper<ScheduledRepeatingJob> jobRowMapper;

    private SimpleTableAccessTemplate<ScheduledRepeatingJob> template;
    
    public SeparableRowMapper<ScheduledRepeatingJob> getJobRowMapper() {
    	
    	SeparableRowMapper<ScheduledRepeatingJob> rowMapper = new SeparableRowMapper<ScheduledRepeatingJob>(yukonJobBaseRowMapper) {
            protected ScheduledRepeatingJob createObject(ResultSet rs) throws SQLException {
                ScheduledRepeatingJob job = new ScheduledRepeatingJob();
                return job;
            }

            protected void mapRow(ResultSet rs, ScheduledRepeatingJob job) throws SQLException {
                job.setCronString(rs.getString("cronString"));
            }
        };
        
        return rowMapper;
    }
    
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        
        jobRowMapper = getJobRowMapper();
        
        template = new SimpleTableAccessTemplate<ScheduledRepeatingJob>(jdbcTemplate, nextValueHelper);
        template.withTableName("JobScheduledRepeating");
        template.withPrimaryKeyField("jobId");
        template.withFieldMapper(jobFieldMapper); 
    }

    public Set<ScheduledRepeatingJob> getAll() {
        String sql = 
            "SELECT * " +
            "FROM JobScheduledRepeating JSR " +
            "JOIN Job J on J.JobId = JSR.JobId";
        
        List<ScheduledRepeatingJob> jobList = jdbcTemplate.query(sql, jobRowMapper);
        Set<ScheduledRepeatingJob> jobSet = new HashSet<ScheduledRepeatingJob>(jobList);
        
        return jobSet;
    }
    
    public Set<ScheduledRepeatingJob> getJobsByDefinition(YukonJobDefinition<? extends YukonTask> definition) {
        String sql = 
            "select * " +
            "from JobScheduledRepeating jsr " +
            "join Job on Job.jobId = jsr.jobId " +
            "where Job.beanName = ?";
        
        List<ScheduledRepeatingJob> jobList = jdbcTemplate.query(sql, jobRowMapper, definition.getName());
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
        JobStatusRowMapper<ScheduledRepeatingJob> jobStatusRowMapper = new JobStatusRowMapper<ScheduledRepeatingJob>(jobRowMapper);
        List<JobStatus<ScheduledRepeatingJob>> resultList = jdbcTemplate.query(sql.toString(), jobStatusRowMapper, jobState);
        HashSet<JobStatus<ScheduledRepeatingJob>> resultSet = new HashSet<JobStatus<ScheduledRepeatingJob>>(resultList);
        return resultSet;
    }

    public ScheduledRepeatingJob getById(int id) {

        SeparableRowMapper<ScheduledRepeatingJob> mapper = new SeparableRowMapper<ScheduledRepeatingJob>(yukonJobBaseRowMapper) {
            protected ScheduledRepeatingJob createObject(ResultSet rs) throws SQLException {
                ScheduledRepeatingJob job = new ScheduledRepeatingJob();
                return job;
            }

            protected void mapRow(ResultSet rs, ScheduledRepeatingJob job) throws SQLException {
                job.setCronString(rs.getString("cronString"));
            }
        };
        
        String sql = 
            "SELECT * " +
            "FROM JobScheduledRepeating jsr " +
            "JOIN Job ON Job.jobId = jsr.jobId " +
            "WHERE Job.jobId = ?";
        ScheduledRepeatingJob job = jdbcTemplate.queryForObject(sql, mapper, id);
        
        return job;
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    public void save(ScheduledRepeatingJob repeatingJob) {
        try {
            // create the Job entry first
            insertJob(repeatingJob);
            // create JobScheduledRepeating entry
            template.insert(repeatingJob);

        } catch (RuntimeException e) {
            // if an exception gets thrown, the transaction will be rolled back
            // in this case we want to reset the id to null
            repeatingJob.setId(null);
            throw e;
        }        
    }
    
    
    
    @Required
    public void setYukonJobMapper(YukonJobBaseRowMapper yukonJobMapper) {
        this.yukonJobBaseRowMapper = yukonJobMapper;
    }

}
