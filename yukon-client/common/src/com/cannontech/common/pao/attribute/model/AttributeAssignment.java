package com.cannontech.common.pao.attribute.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.point.PointType;

public class AttributeAssignment {
   
    private int id;
    private int attributeId;
    private PaoType deviceType;
    private int pointOffset;
    private PointType pointType;
     
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public PaoType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(PaoType deviceType) {
        this.deviceType = deviceType;
    }

    public int getPointOffset() {
        return pointOffset;
    }

    public void setPointOffset(int pointOffset) {
        this.pointOffset = pointOffset;
    }
    
    public PointType getPointType() {
        return pointType;
    }

    public void setPointType(PointType pointType) {
        this.pointType = pointType;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}
