package com.cannontech.cbc.model;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * A class to allow the easy traversing of the a zone hierarchy from top down.
 */
public class ZoneHierarchy implements NavigableHierarchy<Zone> {
    private Zone zone = null;
    private List<ZoneHierarchy> childZones = Lists.newArrayList();
        
    public Zone getZone() {
        return zone;
    }
    
    public void setZone(Zone zone) {
        this.zone = zone;
    }
    
    public void setChildZones(List<ZoneHierarchy> childZones) {
        this.childZones = childZones;
    }
    
    @Override
    public List<ZoneHierarchy> getChildren() {
        return childZones;
    }
    
    @Override
    public Zone getNode() {
        return zone;
    }
}
