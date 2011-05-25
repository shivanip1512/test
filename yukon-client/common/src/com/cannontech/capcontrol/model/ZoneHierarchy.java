package com.cannontech.capcontrol.model;

import java.util.List;

import com.cannontech.common.util.NavigableHierarchy;
import com.google.common.collect.Lists;

/**
 * A class to allow the easy traversing of the a zone hierarchy from top down.
 */
public class ZoneHierarchy implements NavigableHierarchy<ZoneDto> {
    private ZoneDto zone = null;
    private List<ZoneHierarchy> childZones = Lists.newArrayList();
        
    public ZoneDto getZone() {
        return zone;
    }
    
    public void setZone(ZoneDto zone) {
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
    public ZoneDto getNode() {
        return zone;
    }
}
