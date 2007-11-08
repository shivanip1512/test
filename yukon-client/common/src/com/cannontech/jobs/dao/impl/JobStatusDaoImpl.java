package com.cannontech.jobs.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.database.RowAndFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.model.JobStatus;

public class JobStatusDaoImpl implements JobStatusDao, InitializingBean {
    protected SimpleJdbcOperations jdbcTemplate;
    protected NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<JobStatus> template;

    public void saveOrUpdate(JobStatus<?> status) {
        template.save(status);
    }

    private RowAndFieldMapper<JobStatus> jobStatusFieldMapper = new RowAndFieldMapper<JobStatus>() {

        public void extractValues(MapSqlParameterSource p, JobStatus status) {
            int jobId = status.getJob().getId();
            p.addValue("jobId", jobId);
            Date startTime = status.getStartTime();
            p.addValue("startTime", startTime);
            Date stopTime = status.getStopTime();
            p.addValue("stopTime", stopTime, Types.TIMESTAMP);
            String jobState = status.getJobState().name();
            p.addValue("jobState", jobState);
            String message = status.getMessage();
            p.addValue("message", message);   
        }

        public Number getPrimaryKey(JobStatus status) {
            return status.getId();
        }

        public void setPrimaryKey(JobStatus status, int value) {
            status.setId(value);
        }

        public JobStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
            return null;
        }
        
    };
    
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<JobStatus>(jdbcTemplate, nextValueHelper);
        template.withTableName("JobStatus");
        template.withPrimaryKeyField("jobStatusId");
        template.withFieldMapper(jobStatusFieldMapper); 
    }

}
