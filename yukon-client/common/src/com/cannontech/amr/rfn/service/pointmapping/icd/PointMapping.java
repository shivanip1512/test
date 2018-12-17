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
    public boolean isMappedFor(PaoType type) {
        return pointDefinition.isMapped() && !excludedTypes.contains(type);
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
        return "NormalizedPoint [pointDefinition=" + pointDefinition + ", basePoint=" + basePoint + "]";
    }
}