package com.cannontech.common.scheduledFileExport.dao.impl;

import javax.annotation.PostConstruct;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.scheduledFileExport.dao.ScheduledFileExportDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class ScheduledFileExportDaoImpl implements ScheduledFileExportDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private ConfigurationSource configurationSource;

    private int initDays;

    @PostConstruct
    public void init() {
        initDays = configurationSource.getInteger("SINCE_LAST_RUN_INIT_DAYS", 7);
    }

    @Override
    public void setRphIdForJob(int jobId, int jobGroupId, long rphId) {
        if (hasEntry(jobGroupId)) {
            updateRphIdForJob(jobId, jobGroupId, rphId);
        } else {
            insertRphIdForJob(jobId, jobGroupId, rphId);
        }
    }

    private void insertRphIdForJob(int jobId, int jobGroupId, long rphId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("RawPointHistoryDependentJob");
        sink.addValue("JobId", jobId);
        sink.addValue("JobGroupId", jobGroupId);
        sink.addValue("RawPointHistoryId", rphId);

        yukonJdbcTemplate.update(sql);
    }

    private void updateRphIdForJob(int jobId, int jobGroupId, long rphId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.update("RawPointHistoryDependentJob");
        sink.addValue("JobId", jobId);
        sink.addValue("JobGroupId", jobGroupId);
        sink.addValue("RawPointHistoryId", rphId);
        sql.append("WHERE JobGroupId").eq(jobGroupId);

        yukonJdbcTemplate.update(sql);
    }

    private boolean hasEntry(int jobGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM RawPointHistoryDependentJob");
        sql.append("WHERE JobGroupId").eq(jobGroupId);

        int result = yukonJdbcTemplate.queryForInt(sql);
        return result > 0;
    }

    @Override
    public long getLastRphIdByJobId(int jobGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RawPointHistoryId");
        sql.append("FROM RawPointHistoryDependentJob");
        sql.append("WHERE JobGroupId").eq(jobGroupId);

        long result;
        try {
            result = yukonJdbcTemplate.queryForLong(sql);
        } catch (DataAccessException e) {
            result = getOldestRphIdInDaysPrevious();
        }
        return result;
    }

    private long getOldestRphIdInDaysPrevious() {
        Instant xDaysAgo = Instant.now().minus(Duration.standardDays(initDays));
        Instant now = Instant.now();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select min(ChangeId)");
        sql.append("from RawPointHistory");
        sql.append("where Timestamp").gte(xDaysAgo);
        sql.append(  "and Timestamp").lt(now);

        long changeId = yukonJdbcTemplate.queryForLong(sql);
        if (changeId == 0) {
            // No changeIds from the past X days. Just return the most recent
            // one.
            return rawPointHistoryDao.getMaxChangeId();
        }
        return changeId;
    }
}
