package com.cannontech.web.tools.mapping.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geojson.FeatureCollection;

import com.cannontech.web.tools.mapping.model.NetworkMapFilter.Legend;

public class NetworkMap {
    
    private List<Legend> legend = new ArrayList<>();
    private HashMap<String, FeatureCollection> mappedDevices = new HashMap<>();
    
    public HashMap<String, FeatureCollection> getMappedDevices() {
        return mappedDevices;
    }
    public void setMappedDevices(HashMap<String, FeatureCollection> mappedDevices) {
        this.mappedDevices = mappedDevices;
    }
    public List<Legend> getLegend() {
        return legend;
    }
    public void setLegend(List<Legend> legend) {
        this.legend = legend;
    }
}
