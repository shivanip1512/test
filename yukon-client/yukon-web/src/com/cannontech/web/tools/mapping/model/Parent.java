package com.cannontech.web.tools.mapping.model;

import org.geojson.FeatureCollection;

import com.cannontech.common.rfn.message.network.ParentData;
import com.cannontech.common.rfn.model.RfnDevice;

public class Parent {

    private RfnDevice device;
    private FeatureCollection location;
    private ParentData data;

    public Parent(RfnDevice device, FeatureCollection location, ParentData data) {
        this.location = location;
        this.data = data;
        this.device = device;
    }

    public ParentData getData() {
        return data;
    }

    public FeatureCollection getLocation() {
        return location;
    }

    public RfnDevice getDevice() {
        return device;
    }
}
