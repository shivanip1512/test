package com.cannontech.web.tools.mapping;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;

public class MappingColorCollection {

    private DeviceCollection deviceCollection;
    private String color;
    private String labelKey;

    public MappingColorCollection(DeviceCollection deviceCollection, String color, String labelKey) {
        this.deviceCollection = deviceCollection;
        this.color = color;
        this.labelKey = labelKey;
    }

    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }

    public void setDeviceCollection(DeviceCollection deviceCollection) {
        this.deviceCollection = deviceCollection;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public void setLabelKey(String labelKey) {
        this.labelKey = labelKey;
    }
}