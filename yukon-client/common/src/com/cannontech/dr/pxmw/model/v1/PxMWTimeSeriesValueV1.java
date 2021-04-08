package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PxMWTimeSeriesValueV1 implements Serializable {
    private int timestamp;
    private Instant value;

    @JsonCreator
    public PxMWTimeSeriesValueV1(@JsonProperty("t") int timestamp, @JsonProperty("v") String value) {
        this.timestamp = timestamp;
        this.value = Instant.ofEpochSecond(timestamp);
    }

    @JsonProperty("t")
    public int getTimestamp() {
        return timestamp;
    }

    @JsonProperty("v")
    public Instant getValue() {
        return value;
    }
}
