package com.cannontech.web.tools.mapping.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.geojson.FeatureCollection;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter.Legend;

public class NetworkMap {
    
    private Set<Legend> legend = new TreeSet<>(Comparator.comparing(Legend::getOrder));
    private HashMap<String, FeatureCollection> mappedDevices = new HashMap<>();
    private List<SimpleDevice> devicesWithoutLocation = new ArrayList<>();
    
    public HashMap<String, FeatureCollection> getMappedDevices() {
        return mappedDevices;
    }
    public void setMappedDevices(HashMap<String, FeatureCollection> mappedDevices) {
        this.mappedDevices = mappedDevices;
    }
    public Set<Legend> getLegend() {
        return legend;
    }
    public void setLegend(Set<Legend> legend) {
        this.legend = legend;
    }
    public List<SimpleDevice> getDevicesWithoutLocation() {
        return devicesWithoutLocation;
    }
    public void setDevicesWithoutLocation(List<SimpleDevice> devicesWithoutLocation) {
        this.devicesWithoutLocation = devicesWithoutLocation;
    }

    //used in JS
    public int getTotalDevices() {
        int totalNumber = 0;
        for (FeatureCollection feature : mappedDevices.values()) {
            totalNumber += feature.getFeatures().size();
        }
        return totalNumber + devicesWithoutLocation.size();
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(System.getProperty("line.separator"));
        builder.append(legend);
        mappedDevices.forEach((k,v) ->{
            builder.append(System.getProperty("line.separator"));
            builder.append(" color:"+ k+" - devices:"+v.getFeatures().size());
        });
        builder.append("devicesWithoutLocation="+getDevicesWithoutLocation().size());
        return builder.toString();
    }
}
