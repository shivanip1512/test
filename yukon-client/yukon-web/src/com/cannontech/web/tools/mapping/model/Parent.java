package com.cannontech.web.tools.mapping.model;

import org.geojson.FeatureCollection;

import com.cannontech.common.rfn.message.network.ParentData;

public class Parent {

    private int deviceId;
    private FeatureCollection location;
    private ParentData data;

    public Parent(int deviceId, FeatureCollection location, ParentData data) {
        this.location = location;
        this.data = data;
        this.deviceId = deviceId;
    }

    public ParentData getData() {
        return data;
    }

    public FeatureCollection getLocation() {
        return location;
    }

    public int getDeviceId() {
        return deviceId;
    }
}
