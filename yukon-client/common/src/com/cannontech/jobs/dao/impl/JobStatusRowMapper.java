package com.cannontech.jobs.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.YukonJob;

final class JobStatusRowMapper<T extends YukonJob> implements
    ParameterizedRowMapper<JobStatus<T>> {
    private final ParameterizedRowMapper<T> internalJobRowMapper;

    public JobStatusRowMapper(ParameterizedRowMapper<T> jobRowMapper) {
        internalJobRowMapper = jobRowMapper;
    }

    public JobStatus<T> mapRow(ResultSet rs, int rowNum) throws SQLException {
        JobStatus<T> jobStatus = new JobStatus<T>();
        jobStatus.setId(rs.getInt("jobStatusId"));
        String jobStateStr = rs.getString("jobState");
        JobState jobState = JobState.valueOf(jobStateStr);
        jobStatus.setJobState(jobState);
        jobStatus.setStartTime(rs.getTimestamp("startTime"));
        jobStatus.setStopTime(rs.getTimestamp("stopTime"));
        jobStatus.setMessage(rs.getString("message"));

        T job = internalJobRowMapper.mapRow(rs, rowNum);
        jobStatus.setJob(job);

        return jobStatus;
    }
}