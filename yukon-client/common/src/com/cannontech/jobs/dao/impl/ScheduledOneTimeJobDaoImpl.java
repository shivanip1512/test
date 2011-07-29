package com.cannontech.jobs.dao.impl;

import java.sql.ResultSet;
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
            protected ScheduledOneTimeJob createObject(ResultSet rs) throws SQLException {
                ScheduledOneTimeJob job = new ScheduledOneTimeJob();
                return job;
            }

            protected void mapRow(ResultSet rs, ScheduledOneTimeJob job) throws SQLException {
                job.setStartTime(rs.getTimestamp("startTime"));
            }
        };
        
        template =
            new SimpleTableAccessTemplate<ScheduledOneTimeJob>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("JobScheduledOneTime");
        template.setPrimaryKeyField("jobId");
        template.setFieldMapper(jobFieldMapper);
    }

    public Set<ScheduledOneTimeJob> getAll() {
        String sql =
            "select * " + "from JobScheduledOneTime jsr " + "join Job on Job.jobId = jsr.jobId";

        List<ScheduledOneTimeJob> jobList = yukonJdbcTemplate.query(sql, jobRowMapper);
        Set<ScheduledOneTimeJob> jobSet = new HashSet<ScheduledOneTimeJob>(jobList);

        return jobSet;
    }
    
    public Set<ScheduledOneTimeJob> getJobsByDefinition(YukonJobDefinition<? extends YukonTask> definition) {
        String sql =
            "select * " + 
            "from JobScheduledOneTime jsr " + 
            "join Job on Job.jobId = jsr.jobId " +
            "where Job.beanName = ?";

        List<ScheduledOneTimeJob> jobList = 
            yukonJdbcTemplate.query(sql, jobRowMapper, definition.getName());
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
        List<JobStatus<ScheduledOneTimeJob>> resultList =
            yukonJdbcTemplate.query(sql.toString(), 
                                    new JobStatusRowMapper<ScheduledOneTimeJob>(jobRowMapper), 
                                    jobState);
        HashSet<JobStatus<ScheduledOneTimeJob>> resultSet =
            new HashSet<JobStatus<ScheduledOneTimeJob>>(resultList);
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
        List<ScheduledOneTimeJob> resultList =
            yukonJdbcTemplate.query(sql.toString(), jobRowMapper);
        HashSet<ScheduledOneTimeJob> resultSet =
            new HashSet<ScheduledOneTimeJob>(resultList);
        return resultSet;
    }

    public ScheduledOneTimeJob getById(int id) {

        SeparableRowMapper<ScheduledOneTimeJob> mapper = new SeparableRowMapper<ScheduledOneTimeJob>(yukonJobBaseRowMapper) {
            protected ScheduledOneTimeJob createObject(ResultSet rs) throws SQLException {
                ScheduledOneTimeJob job = new ScheduledOneTimeJob();
                return job;
            }

            protected void mapRow(ResultSet rs, ScheduledOneTimeJob job) throws SQLException {
                job.setStartTime(rs.getTimestamp("startTime"));
            }
        };
        
        String sql = 
            "SELECT * " +
            "FROM JobScheduledOneTime jso " +
            "JOIN Job ON Job.jobId = jso.jobId " +
            "WHERE Job.jobId = ?";
        ScheduledOneTimeJob job = yukonJdbcTemplate.queryForObject(sql, mapper, id);
        
        return job;
    }

    public JobDisabledStatus getJobDisabledStatusById(int jobId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Job.Disabled");
        sql.append("FROM JobScheduledOneTime JSO");
        sql.append("JOIN Job ON Job.jobId = JSO.jobId");
        sql.append("WHERE Job.jobId").eq(jobId);
        JobDisabledStatus result = yukonJdbcTemplate.queryForObject(sql, jobDisabledStatusRowMapper);
        return result;
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
