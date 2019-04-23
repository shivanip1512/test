package com.cannontech.web.tools.mapping.model;

import java.util.ArrayList;
import java.util.List;

import org.geojson.FeatureCollection;

import com.cannontech.web.tools.mapping.model.NetworkMapFilter.Color;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter.Legend;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class NetworkMap {
    
    private List<Legend> legend = new ArrayList<>();
    private Multimap<Color, FeatureCollection> mappedDevices = HashMultimap.create();
    
    public Multimap<Color, FeatureCollection> getMappedDevices() {
        return mappedDevices;
    }
    public void setMappedDevices(Multimap<Color, FeatureCollection> mappedDevices) {
        this.mappedDevices = mappedDevices;
    }
    public List<Legend> getLegend() {
        return legend;
    }
    public void setLegend(List<Legend> legend) {
        this.legend = legend;
    }
}
