package com.cannontech.dr.ecobee.message;

import com.cannontech.dr.ecobee.message.partial.Status;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class RuntimeReportJobResponse extends BaseResponse {
    private final String jobId;
    private final EcobeeJobStatus jobStatus;
  

    @JsonCreator
    public RuntimeReportJobResponse(@JsonProperty("jobId") String jobId,
            @JsonProperty("jobStatus") EcobeeJobStatus jobStatus, @JsonProperty("status") Status status) {
        super(status);
        this.jobId = jobId;
        this.jobStatus = jobStatus;
    }

    public String getJobId() {
        return jobId;
    }

    public EcobeeJobStatus getJobStatus() {
        return jobStatus;
    }
}
