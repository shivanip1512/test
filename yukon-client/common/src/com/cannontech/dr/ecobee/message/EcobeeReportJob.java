package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class EcobeeReportJob {

    private final String jobId;
    private final EcobeeJobStatus status;
    private final String message;
    private final String[] files;

    @JsonCreator
    public EcobeeReportJob(@JsonProperty("jobId") String jobId, @JsonProperty("status") EcobeeJobStatus status,
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


    public EcobeeJobStatus getStatus() {
        return status;
    }
}
