package com.cannontech.web.scheduledFileExport;

import com.cannontech.jobs.model.JobState;

public interface ScheduledFileExportJobData extends Comparable<ScheduledFileExportJobData> {
    public int getId();
    public String getName();
    public int getJobGroupId();
    public String getCronString();
    public JobState getJobState();
}
