package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PxMWTimeSeriesDeviceResultV1 implements Serializable{
    private String deviceId;
    private List<PxMWTimeSeriesResultV1> results;
    
    @JsonCreator
    public PxMWTimeSeriesDeviceResultV1(@JsonProperty("device_id") String deviceId, @JsonProperty("results") List<PxMWTimeSeriesResultV1> results) {
        this.deviceId = deviceId;
        this.results = results;
    }
    
    @JsonProperty("device_id")
    public String getDeviceId() {
        return deviceId;
    }
    
    @JsonProperty("results")
    public List<PxMWTimeSeriesResultV1> getResults() {
        return results;
    }
}
