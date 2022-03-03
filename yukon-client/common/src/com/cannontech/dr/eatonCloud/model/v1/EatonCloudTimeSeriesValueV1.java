package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EatonCloudTimeSeriesValueV1 implements Serializable {
    private long timestamp;
    private String value;

    @JsonCreator
    public EatonCloudTimeSeriesValueV1(@JsonProperty("t") long timestamp, @JsonProperty("v") String value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    @JsonProperty("t")
    public long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("v")
    public String getValue() {
        return value;
    }
}
