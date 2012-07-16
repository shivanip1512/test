package com.cannontech.jobs.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.jobs.dao.ScheduledOneTimeJobDao;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledOneTimeJob;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.spring.SeparableRowMapper;

public class ScheduledOneTimeJobDaoImpl extends JobDaoBase implements ScheduledOneTimeJobDao {
    private YukonJobBaseRowMapper yukonJobBaseRowMapper;
    private SimpleTableAccessTemplate<ScheduledOneTimeJob> template;
    
    private final FieldMapper<ScheduledOneTimeJob> jobFieldMapper =
        new FieldMapper<ScheduledOneTimeJob>() {
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

    private SeparableRowMapper<ScheduledOneTimeJob> jobRowMapper;


    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        
        jobRowMapper = new SeparableRowMapper<ScheduledOneTimeJob>(yukonJobBaseRowMapper) {
            protected ScheduledOneTimeJob createObject(YukonResultSet rs) throws SQLException {
                ScheduledOneTimeJob job = new ScheduledOneTimeJob();
                return job;
            }

            protected void mapRow(YukonResultSet rs, ScheduledOneTimeJob job) throws SQLException {
                job.setStartTime(rs.getDate("startTime"));
            }
        };
        
        template =
            new SimpleTableAccessTemplate<ScheduledOneTimeJob>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("JobScheduledOneTime");
        template.setPrimaryKeyField("jobId");
        template.setFieldMapper(jobFieldMapper);
    }

    public Set<ScheduledOneTimeJob> getAll() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from JobScheduledOneTime jsr");
        sql.append(  "join Job on Job.jobId = jsr.jobId");

        List<ScheduledOneTimeJob> jobList = yukonJdbcTemplate.query(sql, jobRowMapper);
        Set<ScheduledOneTimeJob> jobSet = new HashSet<ScheduledOneTimeJob>(jobList);

        return jobSet;
    }
    
    public Set<ScheduledOneTimeJob> getJobsByDefinition(YukonJobDefinition<? extends YukonTask> definition) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * ");
        sql.append("from JobScheduledOneTime jsr "); 
        sql.append(  "join Job on Job.jobId = jsr.jobId ");
        sql.append("where Job.beanName").eq(definition.getName());

        List<ScheduledOneTimeJob> jobList = yukonJdbcTemplate.query(sql, jobRowMapper);
        Set<ScheduledOneTimeJob> jobSet = new HashSet<ScheduledOneTimeJob>(jobList);
        
        return jobSet;
    }

    public Set<JobStatus<ScheduledOneTimeJob>> getAllUnfinished() {
        String jobState = JobState.STARTED.name();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from JobStatus js");
        sql.append("join JobScheduledOneTime jso on js.jobid = jso.jobid");
        sql.append("join Job j on jso.jobid = j.jobid");
        sql.append("where jobState").eq(jobState);
        
        List<JobStatus<ScheduledOneTimeJob>> resultList = yukonJdbcTemplate.query(sql, 
                                    new JobStatusRowMapper<ScheduledOneTimeJob>(jobRowMapper));
        
        HashSet<JobStatus<ScheduledOneTimeJob>> resultSet = new HashSet<JobStatus<ScheduledOneTimeJob>>(resultList);
        return resultSet;
    }
    
    @Override
    public Set<ScheduledOneTimeJob> getAllUnstarted() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select distinct j.*, jso.*");
        sql.append("from JobScheduledOneTime jso");
        sql.append("join Job j on jso.jobid = j.jobid");
        sql.append("left join JobStatus js on js.jobid = j.jobid");
        sql.append("where js.jobid is null");
        
        List<ScheduledOneTimeJob> resultList = yukonJdbcTemplate.query(sql, jobRowMapper);
        HashSet<ScheduledOneTimeJob> resultSet = new HashSet<ScheduledOneTimeJob>(resultList);
        
        return resultSet;
    }

    public ScheduledOneTimeJob getById(int id) {

        SeparableRowMapper<ScheduledOneTimeJob> mapper = new SeparableRowMapper<ScheduledOneTimeJob>(yukonJobBaseRowMapper) {
            protected ScheduledOneTimeJob createObject(YukonResultSet rs) throws SQLException {
                ScheduledOneTimeJob job = new ScheduledOneTimeJob();
                return job;
            }

            protected void mapRow(YukonResultSet rs, ScheduledOneTimeJob job) throws SQLException {
                job.setStartTime(rs.getDate("startTime"));
            }
        };
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM JobScheduledOneTime jso");
        sql.append("JOIN Job ON Job.jobId = jso.jobId");
        sql.append("WHERE Job.jobId").eq(id);
        ScheduledOneTimeJob job = yukonJdbcTemplate.queryForObject(sql, mapper);
        
        return job;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void save(ScheduledOneTimeJob oneTimeJob) {
        try {
            // create the Job entry first
            insertJob(oneTimeJob);
            // create ScheduledOneTimeJob entry
            template.insert(oneTimeJob);

        } catch (RuntimeException e) {
            // if an exception gets thrown, the transaction will be rolled back
            // in this case we want to reset the id to null
            oneTimeJob.setId(null);
            throw e;
        }
    }

    @Required
    public void setYukonJobMapper(YukonJobBaseRowMapper yukonJobBaseRowMapper) {
        this.yukonJobBaseRowMapper = yukonJobBaseRowMapper;
    }

}