package com.cannontech.common.pao.attribute.model;

import com.cannontech.database.data.point.PointType;

public class MappableAttribute {

    Attribute attribute;
    PointType pointTypeFilter;
    
    public MappableAttribute(Attribute attribute, PointType pointType) {
        this.attribute = attribute;
        this.pointTypeFilter = pointType;
    }
    
    public Attribute getAttribute() {
        return attribute;
    }
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
    public PointType getPointTypeFilter() {
        return pointTypeFilter;
    }
    public void setPointTypeFilter(PointType pointTypeFilter) {
        this.pointTypeFilter = pointTypeFilter;
    }
}
