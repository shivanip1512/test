package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EatonCloudTimeSeriesDataRequestV1 implements Serializable {

    private final List<EatonCloudTimeSeriesDeviceV1> devices;
    private final String startTime;
    private final String endTime;

    @JsonCreator
    public EatonCloudTimeSeriesDataRequestV1(@JsonProperty("devices") List<EatonCloudTimeSeriesDeviceV1> devices,
            @JsonProperty("start_time") String startTime, @JsonProperty("end_time") String endTime) {
        this.devices = devices;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @JsonProperty("devices")
    public List<EatonCloudTimeSeriesDeviceV1> getDevices() {
        return devices;
    }

    @JsonProperty("start_time")
    public String getStartTime() {
        return startTime;
    }

    @JsonProperty("end_time")
    public String getEndTime() {
        return endTime;
    }
}
