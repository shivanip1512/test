package com.cannontech.deviceReadings.model;

import java.util.List;

public class DeviceReadingsResponse {

    List<DeviceReadingResponse> deviceReadingResponse;

    public List<DeviceReadingResponse> getDeviceReadingResponse() {
        return deviceReadingResponse;
    }

    public void setDeviceReadingResponse(List<DeviceReadingResponse> deviceReadingResponse) {
        this.deviceReadingResponse = deviceReadingResponse;
    }

}
