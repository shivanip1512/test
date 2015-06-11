package com.cannontech.web.capcontrol.area.model;

import com.cannontech.web.capcontrol.models.PaoModel;

public class Area extends PaoModel implements Comparable<Area> {
    
    private Integer voltReductionPoint;
    
    @Override
    public int compareTo(Area area) {
        return this.name.compareTo(area.name);
    }
    
    public Integer getVoltReductionPoint() {
        return voltReductionPoint;
    }
    
    public void setVoltReductionPoint(Integer voltReductionPoint) {
        this.voltReductionPoint = voltReductionPoint;
    }
    
    @Override
    public String toString() {
        return String.format("Area [voltReductionPoint=%s, id=%s, name=%s, description=%s, disabled=%s]", 
                getVoltReductionPoint(), id, name, description, disabled);
    }
    
}