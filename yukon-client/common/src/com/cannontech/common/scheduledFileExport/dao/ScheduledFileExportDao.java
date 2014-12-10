package com.cannontech.common.scheduledFileExport.dao;

public interface ScheduledFileExportDao {

    public void setRphIdForJob(int jobId, int jobGroupId, long rphId);

    public long getLastRphIdByJobId(int jobId);
}
