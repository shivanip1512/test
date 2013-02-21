package com.cannontech.common.pao.definition.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.point.YukonPoint;
import com.cannontech.core.dynamic.PointValueQualityHolder;

public final class PaoPointValue implements YukonPao, YukonPoint {
    
    private final PaoPointIdentifier paoPointIdentifier;
    private final PointValueQualityHolder pointValueQualityHolder;

    private PaoPointValue(PaoPointIdentifier paoPointIdentifier, PointValueQualityHolder pointValueQualityHolder) {
        this.paoPointIdentifier = paoPointIdentifier;
        this.pointValueQualityHolder = pointValueQualityHolder;
    }
    
    public PaoPointIdentifier getPaoPointIdentifier() {
        return paoPointIdentifier;
    }
    
    public PointValueQualityHolder getPointValueQualityHolder() {
        return pointValueQualityHolder;
    }
    
    public static PaoPointValue of(PaoPointIdentifier paoIdentifier, PointValueQualityHolder pointValueQualityHolder) {
        return new PaoPointValue(paoIdentifier, pointValueQualityHolder);
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoPointIdentifier.getPaoIdentifier();
    }

    @Override
    public PointIdentifier getPointIdentifier() {
        return paoPointIdentifier.getPointIdentifier();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result + ((paoPointIdentifier == null) ? 0 : paoPointIdentifier.hashCode());
        result =
            prime * result
                    + ((pointValueQualityHolder == null) ? 0 : pointValueQualityHolder.hashCode());
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
        PaoPointValue other = (PaoPointValue) obj;
        if (paoPointIdentifier == null) {
            if (other.paoPointIdentifier != null)
                return false;
        } else if (!paoPointIdentifier.equals(other.paoPointIdentifier))
            return false;
        if (pointValueQualityHolder == null) {
            if (other.pointValueQualityHolder != null)
                return false;
        } else if (!pointValueQualityHolder.equals(other.pointValueQualityHolder))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("PaoPointValue [paoPointIdentifier=%s, pointValueQualityHolder=%s]", paoPointIdentifier, pointValueQualityHolder);
    }
    
}