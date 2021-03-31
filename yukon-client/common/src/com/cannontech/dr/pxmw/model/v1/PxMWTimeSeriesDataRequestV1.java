package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;
import java.util.List;

import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PxMWTimeSeriesDataRequestV1 implements Serializable {

    private final List<PxMWTimeSeriesDeviceV1> devices;
    private final String startTime;
    private final String endTime;

    @JsonCreator
    public PxMWTimeSeriesDataRequestV1(@JsonProperty("devices") List<PxMWTimeSeriesDeviceV1> devices,
            @JsonProperty("start_time") Instant startTime, @JsonProperty("end_time") Instant endTime) {
        this.devices = devices;
        this.startTime = startTime.toDateTime().toString();
        this.endTime = endTime.toDateTime().toString();
    }

    @JsonProperty("devices")
    public List<PxMWTimeSeriesDeviceV1> getDevices() {
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
