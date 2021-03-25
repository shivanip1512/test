package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PxMWTimeSeriesValueV1 implements Serializable {
    public int t;
    public String v;

    @JsonCreator
    public PxMWTimeSeriesValueV1(@JsonProperty("t") int t, @JsonProperty("v") String v) {
        this.t = t;
        this.v = v;
    }

    @JsonProperty("t")
    public int getT() {
        return t;
    }

    @JsonProperty("v")
    public String getV() {
        return v;
    }
}
