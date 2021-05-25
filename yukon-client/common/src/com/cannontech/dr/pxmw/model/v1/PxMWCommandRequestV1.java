package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PxMWCommandRequestV1 implements Serializable {
    private final String method;
    private final Map<String, Object> params;

    @JsonCreator
    public PxMWCommandRequestV1(@JsonProperty("method") String method, @JsonProperty("params") Map<String, Object> params) {
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