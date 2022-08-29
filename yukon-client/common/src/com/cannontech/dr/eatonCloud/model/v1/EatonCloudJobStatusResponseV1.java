package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EatonCloudJobStatusResponseV1 implements Serializable {
    private final String jobGuid;
    private final List<String> successes;
    private final Map<String, EatonCloudJobDeviceErrorV1> failures;

    @JsonCreator
    public EatonCloudJobStatusResponseV1(@JsonProperty("id") String jobGuid, @JsonProperty("successes") List<String> successes,
            @JsonProperty("failures") Map<String, EatonCloudJobDeviceErrorV1> failures) {
        this.jobGuid = jobGuid;
        this.successes = successes;
        this.failures = failures;
    }
    
    @JsonProperty("id")
    public String getJobGuid() {
        return jobGuid;
    }

    @JsonProperty("successes")
    public List<String> getSuccesses() {
        return successes;
    }

    @JsonProperty("failures")
    public Map<String, EatonCloudJobDeviceErrorV1> getFailures() {
        return failures;
    }
}
