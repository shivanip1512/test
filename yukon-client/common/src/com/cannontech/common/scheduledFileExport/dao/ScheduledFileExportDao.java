package com.cannontech.common.scheduledFileExport.dao;

public interface ScheduledFileExportDao {
	
	public void setRphIdForJob(int jobId, long rphId);
	
	public long getLastRphIdByJobId(int jobId);
}
