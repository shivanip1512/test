package com.cannontech.dr.ecobee.message;

import com.cannontech.dr.JsonSerializers.FROM_JOB_STATUS;
import com.cannontech.dr.JsonSerializers.TO_JOB_STATUS;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

@JsonSerialize(using = TO_JOB_STATUS.class)
@JsonDeserialize(using = FROM_JOB_STATUS.class)
public enum JobStatus {

    QUEUED("queued"), 
    PROCESSING("processing"), 
    COMPLETED("completed"), 
    CANCELLED("cancelled"), 
    ERROR("error");

    private String status;
    private static final ImmutableMap<String, JobStatus> jobStatusStrings;

    static {
        final Builder<String, JobStatus> b = ImmutableMap.builder();
        for (JobStatus status : values()) {
            b.put(status.status, status);
        }
        jobStatusStrings = b.build();
    }

    private JobStatus(String status) {
        this.status = status;
    }

    public static JobStatus fromEcobeeStatusString(String jobStatus) {
        return jobStatusStrings.get(jobStatus);
    }

    public String getEcobeeStatusString() {
        return status;
    }

}
