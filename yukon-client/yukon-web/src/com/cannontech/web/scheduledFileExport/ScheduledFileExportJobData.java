package com.cannontech.web.scheduledFileExport;

public interface ScheduledFileExportJobData extends Comparable<ScheduledFileExportJobData>{
	public int getId();
	public String getName();
	public int getJobGroupId();
	public String getCronString();
}
