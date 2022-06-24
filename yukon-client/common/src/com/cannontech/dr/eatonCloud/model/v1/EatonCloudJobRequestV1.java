package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EatonCloudJobRequestV1 implements Serializable {
    private final List<String> deviceGuids;
    private final String method;
    private final Map<String, Object> params;

    @JsonCreator
    public EatonCloudJobRequestV1(@JsonProperty("devices") List<String> deviceGuids, @JsonProperty("method") String method,
            @JsonProperty("params") Map<String, Object> params) {
        this.method = method;
        this.params = params;
        this.deviceGuids = deviceGuids;
    }

    @JsonProperty("method")
    public String getMethod() {
        return method;
    }

    @JsonProperty("params")
    public Map<String, Object> getParams() {
        return params;
    }

    @JsonProperty("devices")
    public List<String> getDeviceGuids() {
        return deviceGuids;
    }
}
