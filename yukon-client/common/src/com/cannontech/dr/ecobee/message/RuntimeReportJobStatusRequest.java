package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RuntimeReportJobStatusRequest {

    protected final String jobId;

    @JsonCreator
    public RuntimeReportJobStatusRequest(@JsonProperty("jobId") String jobId) {
        this.jobId = jobId;
    }

    public String getJobId() {
        return jobId;
    }

}
