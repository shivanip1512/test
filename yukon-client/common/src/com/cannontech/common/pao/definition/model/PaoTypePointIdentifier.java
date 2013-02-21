package com.cannontech.common.pao.definition.model;

import com.cannontech.common.pao.PaoType;

public final class PaoTypePointIdentifier {

    private final PaoType paoType;
    private final PointIdentifier pointIdentifier;
    
    private PaoTypePointIdentifier(PaoType paoType, PointIdentifier pointIdentifier) {
        this.paoType = paoType;
        this.pointIdentifier = pointIdentifier;
    }
    
    public PaoType getPaoType() {
        return paoType;
    }
    
    public PointIdentifier getPointIdentifier() {
        return pointIdentifier;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((paoType == null) ? 0 : paoType.hashCode());
        result = prime * result + ((pointIdentifier == null) ? 0 : pointIdentifier.hashCode());
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
        PaoTypePointIdentifier other = (PaoTypePointIdentifier) obj;
        if (paoType != other.paoType)
            return false;
        if (pointIdentifier == null) {
            if (other.pointIdentifier != null)
                return false;
        } else if (!pointIdentifier.equals(other.pointIdentifier))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("PaoTypePointIdentifier [paoType=%s, pointIdentifier=%s]", paoType, pointIdentifier);
    }
    
    public static PaoTypePointIdentifier of(PaoType type, PointIdentifier id) {
        return new PaoTypePointIdentifier(type, id);
    }
    
}