package com.cannontech.common.pao.attribute.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.point.PointType;

public class AttributeAssignment {
   
    private Integer id;
    private CustomAttribute attribute;
    private PaoType deviceType;
    private int pointOffset;
    private PointType pointType;
     
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CustomAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(CustomAttribute attribute) {
        this.attribute = attribute;
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
