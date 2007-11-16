package com.cannontech.jobs.dao.impl;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.YukonJob;

public class JobStatusDaoImpl implements JobStatusDao, InitializingBean {
    protected SimpleJdbcOperations jdbcTemplate;
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
        sql.append("where startTime < ? and stopTime > ?");
        sql.append("order by startTime " + (reverse ? "desc" : ""));
        JobStatusRowMapper<YukonJob> jobStatusRowMapper = new JobStatusRowMapper<YukonJob>(yukonJobBaseRowMapper);
        List<JobStatus<YukonJob>> resultList = jdbcTemplate.query(sql.toString(), jobStatusRowMapper, lateLimit, earlyLimit);
        return resultList;
    }
    
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<JobStatus<?>>(jdbcTemplate, nextValueHelper);
        template.withTableName("JobStatus");
        template.withPrimaryKeyField("jobStatusId");
        template.withFieldMapper(jobStatusFieldMapper); 
    }
    
    @Required
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
