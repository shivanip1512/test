package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ReportJob {

    private final String jobId;
    private final JobStatus status;
    private final String message;
    private final String[] files;

    @JsonCreator
    public ReportJob(@JsonProperty("jobId") String jobId, @JsonProperty("status") JobStatus status,
            @JsonProperty("message") String message, @JsonProperty("files") String[] files) {
        this.jobId = jobId;
        this.status = status;
        this.message = message;
        this.files = files;
    }

    public String getJobId() {
        return jobId;
    }


    public String getMessage() {
        return message;
    }

    public String[] getFiles() {
        return files;
    }


    public JobStatus getStatus() {
        return status;
    }
}
