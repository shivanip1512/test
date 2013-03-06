package com.cannontech.common.scheduledFileExport.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.scheduledFileExport.dao.ScheduledFileExportDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class ScheduledFileExportDaoImpl implements ScheduledFileExportDao {
	@Autowired private YukonJdbcTemplate yukonJdbcTemplate;
	
	@Override
	public void setRphIdForJob(int jobId, long rphId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		SqlParameterSink sink = sql.insertInto("RawPointHistoryDependentJob");
		
		sink.addValue("JobId", jobId);
		sink.addValue("RawPointHistoryId", rphId);
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public long getLastRphIdByJobId(int jobId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT RawPointHistoryId");
		sql.append("FROM RawPointHistoryDependentJob");
		sql.append("WHERE JobId").eq(jobId);
		
		int result = yukonJdbcTemplate.queryForInt(sql);
		return result;
	}
}
