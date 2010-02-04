package com.cannontech.core.dao;

import com.cannontech.common.pao.attribute.model.Attribute;

public class ExtraPaoPointMapping {
    
    private Attribute attribute;
    private int pointId;
    
    public Attribute getAttribute() {
        return attribute;
    }
    
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }
    
    public int getPointId() {
        return pointId;
    }
    
}
