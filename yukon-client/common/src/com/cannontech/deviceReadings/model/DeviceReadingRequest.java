package com.cannontech.deviceReadings.model;

import java.util.List;

public class DeviceReadingRequest {

    private List<DeviceReading> deviceReadings;

    public List<DeviceReading> getDeviceReadings() {
        return deviceReadings;
    }

    public void setDeviceReadings(List<DeviceReading> deviceReadings) {
        this.deviceReadings = deviceReadings;
    }

}
