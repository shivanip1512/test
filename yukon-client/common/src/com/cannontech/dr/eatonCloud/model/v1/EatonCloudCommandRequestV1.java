package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EatonCloudCommandRequestV1 implements Serializable {
    private final String method;
    private final Map<String, Object> params;

    @JsonCreator
    public EatonCloudCommandRequestV1(@JsonProperty("method") String method, @JsonProperty("params") Map<String, Object> params) {
        this.method = method;
        this.params = params;
    }

    @JsonProperty("method")
    public String getMethod() {
        return method;
    }

    @JsonProperty("params")
    public Map<String, Object> getParams() {
        return params;
    }
}
