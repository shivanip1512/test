package com.cannontech.amr.rfn.service.pointmapping.icd;

import java.util.EnumSet;
import java.util.Set;

import com.cannontech.common.pao.PaoType;

public class PointMapping {
    private BasePointDefinition basePoint;
    private PointDefinition pointDefinition;
    private Set<PaoType> excludedTypes = EnumSet.noneOf(PaoType.class);
    
    public PointMapping(PointDefinition pointDefinition, BasePointDefinition basePoint) {
        this.pointDefinition = pointDefinition;
        this.basePoint = basePoint;
    }
    public PointMapping(PointDefinition pointDefinition, BasePointDefinition basePoint, Set<PaoType> excludedTypes) {
        this(pointDefinition, basePoint);
        this.excludedTypes = excludedTypes;
    }
    public BasePointDefinition getBasePoint() {
        return basePoint;
    }
    public PointDefinition getPointDefinition() {
        return pointDefinition;
    }
    public boolean isMappedFor(PaoType type) {
        return pointDefinition.isMapped() && !excludedTypes.contains(type);
    }
    public Set<PaoType> getExcludedTypes() {
        return excludedTypes;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((basePoint == null) ? 0 : basePoint.hashCode());
        result = prime * result + ((pointDefinition == null) ? 0 : pointDefinition.hashCode());
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
        PointMapping other = (PointMapping) obj;
        if (basePoint == null) {
            if (other.basePoint != null)
                return false;
        } else if (!basePoint.equals(other.basePoint))
            return false;
        if (pointDefinition == null) {
            if (other.pointDefinition != null)
                return false;
        } else if (!pointDefinition.equals(other.pointDefinition))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "PointMapping [pointDefinition=" + pointDefinition + ", basePoint=" + basePoint + "]";
    }
    public int compareTo(PointMapping other) {
        int pdCompare = pointDefinition.compareTo(other.pointDefinition);
        if (pdCompare != 0) {
            return pdCompare; 
        }
        if (basePoint == null && other.basePoint == null) {
            return 0;
        }
        if (basePoint == null) {
            return -1;
        }
        if (other.basePoint == null) {
            return 1;
        }
        return basePoint.compareTo(other.basePoint);
    }
}