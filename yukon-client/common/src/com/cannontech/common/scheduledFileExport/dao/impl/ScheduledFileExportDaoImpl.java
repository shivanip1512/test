package com.cannontech.common.scheduledFileExport.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.scheduledFileExport.dao.ScheduledFileExportDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class ScheduledFileExportDaoImpl implements ScheduledFileExportDao {
	@Autowired private YukonJdbcTemplate yukonJdbcTemplate;
	
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
			result = 1;
		}
		return result;
	}
}
