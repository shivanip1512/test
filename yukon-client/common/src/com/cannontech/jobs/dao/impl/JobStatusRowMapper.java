package com.cannontech.jobs.dao.impl;

import java.sql.SQLException;

import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.jobs.model.JobRunStatus;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.YukonJob;

final class JobStatusRowMapper<T extends YukonJob> implements YukonRowMapper<JobStatus<T>> {
    private final YukonRowMapper<T> internalJobRowMapper;

    public JobStatusRowMapper(YukonRowMapper<T> jobRowMapper) {
        internalJobRowMapper = jobRowMapper;
    }

    public JobStatus<T> mapRow(YukonResultSet rs) throws SQLException {
        JobStatus<T> jobStatus = new JobStatus<T>();
        jobStatus.setId(rs.getInt("jobStatusId"));
        String jobStateStr = rs.getString("jobState");
        JobRunStatus jobRunStatus = JobRunStatus.valueOf(jobStateStr);
        jobStatus.setJobRunStatus(jobRunStatus);
        jobStatus.setStartTime(rs.getDate("startTime"));
        jobStatus.setStopTime(rs.getDate("stopTime"));
        jobStatus.setMessage(SqlUtils.convertDbValueToString(rs.getString("message")));

        T job = internalJobRowMapper.mapRow(rs);
        jobStatus.setJob(job);

        return jobStatus;
    }
}