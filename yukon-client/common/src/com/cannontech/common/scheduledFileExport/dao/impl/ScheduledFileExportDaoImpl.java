package com.cannontech.common.scheduledFileExport.dao.impl;

import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.scheduledFileExport.dao.ScheduledFileExportDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class ScheduledFileExportDaoImpl implements ScheduledFileExportDao {
	@Autowired private YukonJdbcTemplate yukonJdbcTemplate;
	@Autowired private RawPointHistoryDao rawPointHistoryDao;
	
	@Override
	public void setRphIdForJob(int jobId, long rphId) {
		if(hasEntry(jobId)) {
			updateRphIdForJob(jobId, rphId);
		} else {
			insertRphIdForJob(jobId, rphId);
		}
	}
	
	private void insertRphIdForJob(int jobId, long rphId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		SqlParameterSink sink = sql.insertInto("RawPointHistoryDependentJob");
		
		sink.addValue("JobId", jobId);
		sink.addValue("RawPointHistoryId", rphId);
		
		yukonJdbcTemplate.update(sql);
	}
	
	private void updateRphIdForJob(int jobId, long rphId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		SqlParameterSink sink = sql.update("RawPointHistoryDependentJob");
		
		sink.addValue("JobId", jobId);
		sink.addValue("RawPointHistoryId", rphId);
		sql.append("WHERE JobId").eq(jobId);
		
		yukonJdbcTemplate.update(sql);
	}
	
	private boolean hasEntry(int jobId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT COUNT(*)");
		sql.append("FROM RawPointHistoryDependentJob");
		sql.append("WHERE JobId").eq(jobId);
		
		int result = yukonJdbcTemplate.queryForInt(sql);
		return result > 0;
	}
	
	@Override
	public long getLastRphIdByJobId(int jobId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT RawPointHistoryId");
		sql.append("FROM RawPointHistoryDependentJob");
		sql.append("WHERE JobId").eq(jobId);
		
		long result;
		try {
			result = yukonJdbcTemplate.queryForLong(sql);
		} catch(DataAccessException e) {
			result = getRphIdUpToSevenDaysPrevious();
		}
		return result;
	}
	
	private long getRphIdUpToSevenDaysPrevious() {
		Instant sevenDaysAgo = Instant.now().minus(Duration.standardDays(7));
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ChangeId");
		sql.append("FROM RawPointHistory");
		sql.append("WHERE Timestamp").gte(sevenDaysAgo);
		sql.append("ORDER BY Timestamp ASC");
		
		List<Long> changeId = yukonJdbcTemplate.queryForLimitedResults(sql, RowMapper.LONG, 1);
		if(changeId.size() > 0) {
			//Return oldest changeId in the past 7 days
			return changeId.get(0);
		} else {
			//No changeIds from the past 7 days. Just return the most recent one.
			return rawPointHistoryDao.getMaxChangeId();
		}
	}
}
