package com.cannontech.common.device.definition.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.core.style.ToStringCreator;

public class PointReference {

    private String pointName = null;

    public PointReference() {
    }

    public PointReference(String pointName) {
        this.pointName = pointName;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public boolean equals(Object obj) {
        if (obj instanceof PointReference == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        PointReference pointRef = (PointReference) obj;
        return new EqualsBuilder().append(pointName, pointRef.getPointName()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(81, 91).append(pointName).toHashCode();
    }
    
    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("pointName", getPointName());
        return tsc.toString();
    }


}
