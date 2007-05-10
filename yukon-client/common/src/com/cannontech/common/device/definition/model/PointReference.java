package com.cannontech.common.device.definition.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class PointReference {

    private String pointName = null;
    private int expectedMsgs = 0;

    public PointReference() {}

    public PointReference(String pointName, int expectedMsgs) {
        this.pointName = pointName;
        this.expectedMsgs = expectedMsgs;
    }

    public int getExpectedMsgs() {
        return expectedMsgs;
    }

    public void setExpectedMsgs(int expectedMsgs) {
        this.expectedMsgs = expectedMsgs;
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
        return new EqualsBuilder().append(pointName, pointRef.getPointName())
                                  .append(expectedMsgs, pointRef.getExpectedMsgs())
                                  .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(81, 91).append(pointName).append(expectedMsgs).toHashCode();
    }

}
