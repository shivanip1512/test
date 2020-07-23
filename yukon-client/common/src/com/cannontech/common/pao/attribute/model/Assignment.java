package com.cannontech.common.pao.attribute.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.point.PointType;

public class Assignment { 
    private Integer attributeId;
    private Integer attributeAssignmentId;
    private PaoType paoType;
    private int offset;
    private PointType pointType;
     
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

    
    public Integer getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Integer attributeId) {
        this.attributeId = attributeId;
    }
    
    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
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
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributeAssignmentId == null) ? 0 : attributeAssignmentId.hashCode());
        result = prime * result + ((attributeId == null) ? 0 : attributeId.hashCode());
        result = prime * result + offset;
        result = prime * result + ((paoType == null) ? 0 : paoType.hashCode());
        result = prime * result + ((pointType == null) ? 0 : pointType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Assignment other = (Assignment) obj;
        if (attributeAssignmentId == null) {
            if (other.attributeAssignmentId != null)
                return false;
        } else if (!attributeAssignmentId.equals(other.attributeAssignmentId))
            return false;
        if (attributeId == null) {
            if (other.attributeId != null)
                return false;
        } else if (!attributeId.equals(other.attributeId))
            return false;
        if (offset != other.offset)
            return false;
        if (paoType != other.paoType)
            return false;
        if (pointType != other.pointType)
            return false;
        return true;
    }
}
