package com.cannontech.database.data.capcontrol;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.database.data.point.PointType;

public class AttributePointMapping implements Comparable<AttributePointMapping>{

    private Attribute attribute;
    private String paoName;
    private String pointName;
    private int pointId;
    private int index;
    private PointType pointType;
    
    public Attribute getAttribute() {
        return attribute;
    }
    
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
    
    public String getPaoName() {
        return paoName;
    }
    
    public void setPaoName(String paoName) {
        this.paoName = paoName;
    }
    
    public String getPointName() {
        return pointName;
    }
    
    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }
    
    public int getPointId() {
        return pointId;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setPointType(PointType pointType) {
        this.pointType = pointType;
    }
    
    public PointType getPointType() {
        return pointType;
    }

    @Override
    public int compareTo(AttributePointMapping o) {
        return this.attribute.getDescription().compareTo(o.getAttribute().getDescription());
    }

}