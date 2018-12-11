package com.cannontech.common.scheduledFileImport;

import com.cannontech.jobs.model.JobState;

/**
 * Base schedule data import object containing parameters required to schedule file import.
 */
public class ScheduledDataImport {

    private String scheduleName;
    private String importPath;
    private String errorFileOutputPath;
    private String cronString;
    private ScheduledImportType importType;
    private Integer jobId;
    private String scheduleDescription;
    private JobState jobState;

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public String getImportPath() {
        return importPath;
    }

    public void setImportPath(String importPath) {
        this.importPath = importPath;
    }

    public String getErrorFileOutputPath() {
        return errorFileOutputPath;
    }

    public void setErrorFileOutputPath(String errorFileOutputPath) {
        this.errorFileOutputPath = errorFileOutputPath;
    }

    public String getCronString() {
        return cronString;
    }

    public void setCronString(String cronString) {
        this.cronString = cronString;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getScheduleDescription() {
        return scheduleDescription;
    }

    public void setScheduleDescription(String scheduleDescription) {
        this.scheduleDescription = scheduleDescription;
    }

    public ScheduledImportType getImportType() {
        return importType;
    }

    public void setImportType(ScheduledImportType importType) {
        this.importType = importType;
    }

    public JobState getJobState() {
        return jobState;
    }

    public void setJobState(JobState jobState) {
        this.jobState = jobState;
    }

}
