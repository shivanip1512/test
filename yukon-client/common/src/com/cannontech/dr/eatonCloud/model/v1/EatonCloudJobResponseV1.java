package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EatonCloudJobResponseV1 implements Serializable {
    private final String jobGuid;

    @JsonCreator
    public EatonCloudJobResponseV1(@JsonProperty("id") String jobGuid) {
        this.jobGuid = jobGuid;
    }

    @JsonProperty("id")
    public String getJobGuid() {
        return jobGuid;
    }
}
