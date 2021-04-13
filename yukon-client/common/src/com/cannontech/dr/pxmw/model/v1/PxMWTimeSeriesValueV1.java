package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PxMWTimeSeriesValueV1 implements Serializable {
    private long timestamp;
    private String value;

    @JsonCreator
    public PxMWTimeSeriesValueV1(@JsonProperty("t") long timestamp, @JsonProperty("v") String value) {
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
