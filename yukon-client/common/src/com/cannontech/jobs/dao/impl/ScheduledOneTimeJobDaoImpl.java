package com.cannontech.jobs.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
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
import com.cannontech.jobs.dao.ScheduledOneTimeJobDao;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledOneTimeJob;
import com.cannontech.spring.SeparableRowMapper;

public class ScheduledOneTimeJobDaoImpl extends JobDaoBase implements ScheduledOneTimeJobDao {
    private FieldMapper<ScheduledOneTimeJob> jobFieldMapper = new FieldMapper<ScheduledOneTimeJob>() {
        public void extractValues(MapSqlParameterSource p, ScheduledOneTimeJob job) {
            Date startTime = job.getStartTime();
            p.addValue("startTime", startTime);
            
        }
        public Number getPrimaryKey(ScheduledOneTimeJob job) {
            return job.getId();
        }
        public void setPrimaryKey(ScheduledOneTimeJob job, int value) {
            job.setId(value);
        }
    };
    
    private SeparableRowMapper<ScheduledOneTimeJob> jobRowMapper = 
        new SeparableRowMapper<ScheduledOneTimeJob>(yukonJobMapper) {
        protected ScheduledOneTimeJob createObject(ResultSet rs) throws SQLException {
            ScheduledOneTimeJob job = new ScheduledOneTimeJob();
            return job;
        }
        protected void mapRow(ResultSet rs, ScheduledOneTimeJob job) throws SQLException {
            job.setStartTime(rs.getTimestamp("startTime"));
        }
    };

    private ParameterizedRowMapper<JobStatus<ScheduledOneTimeJob>> jobStatusRowMapper = 
        new JobStatusRowMapper<ScheduledOneTimeJob>(jobRowMapper);
    
    private SimpleTableAccessTemplate<ScheduledOneTimeJob> template;
    
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        template = new SimpleTableAccessTemplate<ScheduledOneTimeJob>(jdbcTemplate, nextValueHelper);
        template.withTableName("JobScheduledOneTime");
        template.withPrimaryKeyField("jobId");
        template.withFieldMapper(jobFieldMapper); 
    }

    public Set<ScheduledOneTimeJob> getAll() {
        String sql = 
            "select * " +
            "from JobScheduledOneTime jsr " +
            "join Job on Job.jobId = jsr.jobId";
        
        List<ScheduledOneTimeJob> jobList = jdbcTemplate.query(sql, jobRowMapper);
        Set<ScheduledOneTimeJob> jobSet = new HashSet<ScheduledOneTimeJob>(jobList);
        
        return jobSet;
    }

    public Set<JobStatus<ScheduledOneTimeJob>> getAllUnfinished() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from JobStatus js");
        sql.append("join JobScheduledOneTime jso on js.jobid = jso.jobid");
        sql.append("join Job j on jso.jobid = j.jobid");
        sql.append("where jobState = ?");
        String jobState = JobState.STARTED.name();
        List<JobStatus<ScheduledOneTimeJob>> resultList = jdbcTemplate.query(sql.toString(), jobStatusRowMapper, jobState);
        HashSet<JobStatus<ScheduledOneTimeJob>> resultSet = new HashSet<JobStatus<ScheduledOneTimeJob>>(resultList);
        return resultSet;
    }

    public ScheduledOneTimeJob getById(int id) {
        return null;
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    public void save(ScheduledOneTimeJob oneTimeJob) {
        try {
            // create the Job entry first
            saveJob(oneTimeJob);
            // create ScheduledOneTimeJob entry
            template.save(oneTimeJob);

        } catch (RuntimeException e) {
            // if an exception gets thrown, the transaction will be rolled back
            // in this case we want to reset the id to null
            oneTimeJob.setId(null);
            throw e;
        }        
    }

}
