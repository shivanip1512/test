package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EatonCloudTimeSeriesDeviceResultV1 implements Serializable{
    private String deviceId;
    private List<EatonCloudTimeSeriesResultV1> results;
    
    @JsonCreator
    public EatonCloudTimeSeriesDeviceResultV1(@JsonProperty("device_id") String deviceId, @JsonProperty("results") List<EatonCloudTimeSeriesResultV1> results) {
        this.deviceId = deviceId;
        this.results = results;
    }
    
    @JsonProperty("device_id")
    public String getDeviceId() {
        return deviceId;
    }
    
    @JsonProperty("results")
    public List<EatonCloudTimeSeriesResultV1> getResults() {
        return results;
    }
}
