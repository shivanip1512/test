package com.cannontech.deviceReadings.model;

import java.util.List;

public class DeviceReadingRequest {

    private List<DeviceReadingsSelector> deviceReadingsSelectors;

    public List<DeviceReadingsSelector> getDeviceReadingsSelectors() {
        return deviceReadingsSelectors;
    }

    public void setDeviceReadingsSelectors(List<DeviceReadingsSelector> deviceReadingsSelectors) {
        this.deviceReadingsSelectors = deviceReadingsSelectors;
    }

}
