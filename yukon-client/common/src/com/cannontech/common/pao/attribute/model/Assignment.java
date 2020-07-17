package com.cannontech.common.pao.attribute.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.point.PointType;

public abstract class Assignment {
    
    private Integer id;
    private PaoType paoType;
    private int pointOffset;
    private PointType pointType;
     
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    
    public PaoType getPaoType() {
        return paoType;
    }

    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }
  
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((paoType == null) ? 0 : paoType.hashCode());
        result = prime * result + pointOffset;
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
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (paoType != other.paoType)
            return false;
        if (pointOffset != other.pointOffset)
            return false;
        if (pointType != other.pointType)
            return false;
        return true;
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}
