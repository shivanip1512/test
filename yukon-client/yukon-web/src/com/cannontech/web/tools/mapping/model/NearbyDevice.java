package com.cannontech.web.tools.mapping.model;

import org.geojson.FeatureCollection;

import com.cannontech.common.pao.model.PaoDistance;

public class NearbyDevice {
    
    private FeatureCollection location;
    private PaoDistance distance;    

    public FeatureCollection getLocation() {
        return location;
    }

    public void setLocation(FeatureCollection location) {
        this.location = location;
    }

    public PaoDistance getDistance() {
        return distance;
    }

    public void setDistance(PaoDistance distance) {
        this.distance = distance;
    }


}
