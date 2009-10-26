package com.cannontech.common.device.definition.model;

import java.io.Serializable;

import com.cannontech.common.pao.PaoIdentifier;

public class PaoPointIdentifier implements Serializable {
    private PaoIdentifier paoIdentifier;
    private PointIdentifier pointIdentifier;
    
    public PaoPointIdentifier(PaoIdentifier paoIdentifier,
            PointIdentifier pointIdentifier) {
        super();
		this.paoIdentifier = paoIdentifier;
        this.pointIdentifier = pointIdentifier;
    }
    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
		this.paoIdentifier = paoIdentifier;
	}
    public PaoIdentifier getPaoIdentifier() {
		return paoIdentifier;
	}
    public PointIdentifier getPointIdentifier() {
        return pointIdentifier;
    }
    public void setPointIdentifier(PointIdentifier pointIdentifier) {
        this.pointIdentifier = pointIdentifier;
    }
    public PaoTypePointIdentifier getPaoTypePointIdentifier() {
        return new PaoTypePointIdentifier(paoIdentifier.getPaoType(), pointIdentifier);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
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
        PaoPointIdentifier other = (PaoPointIdentifier) obj;
        if (paoIdentifier == null) {
            if (other.paoIdentifier != null)
                return false;
        } else if (!paoIdentifier.equals(other.paoIdentifier))
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
        return paoIdentifier + ":" + pointIdentifier;
    }
    
}
