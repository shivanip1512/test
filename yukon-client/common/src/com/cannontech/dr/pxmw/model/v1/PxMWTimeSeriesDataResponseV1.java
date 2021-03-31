package com.cannontech.dr.pxmw.model.v1;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PxMWTimeSeriesDataResponseV1 {
    private final List<PxMWTimeSeriesDeviceResultV1> msg;
    
    @JsonCreator
    public PxMWTimeSeriesDataResponseV1(@JsonProperty("msg") List<PxMWTimeSeriesDeviceResultV1> msg) {
        this.msg = msg;
    }
    
    @JsonProperty("msg")
    public List<PxMWTimeSeriesDeviceResultV1> getMsg() {
        return msg;
    }
}
