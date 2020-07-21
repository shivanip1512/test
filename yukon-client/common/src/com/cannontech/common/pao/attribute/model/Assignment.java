package com.cannontech.common.pao.attribute.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PointIdentifier;

public abstract class Assignment {
    
    private Integer attributeAssignmentId;
    private PaoType paoType;
    private PointIdentifier pointIdentifier;
     
    public Integer getAttributeAssignmentId() {
        return attributeAssignmentId;
    }

    public void setAttributeAssignmentId(Integer attributeAssignmentId) {
        this.attributeAssignmentId = attributeAssignmentId;
    }

    public PaoType getPaoType() {
        return paoType;
    }

    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }

    public PointIdentifier getPointIdentifier() {
        return pointIdentifier;
    }

    public void setPointIdentifier(PointIdentifier pointIdentifier) {
        this.pointIdentifier = pointIdentifier;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}
