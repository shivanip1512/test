package com.cannontech.database.data.capcontrol;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.core.dao.ExtraPaoPointMapping;
import com.cannontech.database.data.point.PointType;

public class LtcPointMapping implements Comparable<LtcPointMapping>{

    private ExtraPaoPointMapping extraPaoPointMapping;
    private String paoName; /* This is the name of the pao that the point is actually attached to, not the pao it's being mapped to. */
    private String pointName;
    private int index;
    private PointType pointType;
    
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
    
    public Attribute getAttribute() {
        return extraPaoPointMapping.getAttribute();
    }
    
    public void setAttribute(Attribute attribute) {
        this.extraPaoPointMapping.setAttribute(attribute);
    }
    
    public int getPointId() {
        return extraPaoPointMapping.getPointId();
    }
    
    public void setPointId(int pointId) {
        this.extraPaoPointMapping.setPointId(pointId);
    }

    public ExtraPaoPointMapping getExtraPaoPointMapping() {
        return extraPaoPointMapping;
    }

    public void setExtraPaoPointMapping(ExtraPaoPointMapping extraPaoPointMapping) {
        this.extraPaoPointMapping = extraPaoPointMapping;
    }

    @Override
    public int compareTo(LtcPointMapping o) {
        return getAttribute().getDescription().compareTo(o.getAttribute().getDescription());
    }
}