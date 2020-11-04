package com.cannontech.common.point;

import com.cannontech.database.data.point.PointType;

public class PointTypeId {
    private final PointType pointType;
    private final int pointId;
    
    public PointTypeId(PointType pointType, int pointId) {
        this.pointType = pointType;
        this.pointId = pointId;
    }

    public PointType getPointType() {
        return pointType;
    }

    public int getPointId() {
        return pointId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + pointId;
        result = prime * result + ((pointType == null) ? 0 : pointType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PointTypeId other = (PointTypeId) obj;
        if (pointId != other.pointId) {
            return false;
        }
        if (pointType != other.pointType) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("PointTypeId [pointType=%s, pointId=%s]", pointType, pointId);
    }
    
}
