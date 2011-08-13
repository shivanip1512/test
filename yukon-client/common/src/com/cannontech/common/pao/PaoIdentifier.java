package com.cannontech.common.pao;

import java.io.Serializable;

import org.apache.commons.lang.Validate;

public final class PaoIdentifier implements YukonPao, Serializable {

    private static final long serialVersionUID = -592760481960580100L;
    private int paoId;
    private PaoType paoType;

    public PaoIdentifier(int paoId, PaoType paoType) {
        super();
        Validate.notNull(paoType, "paoType must not be null");
        this.paoId = paoId;
        this.paoType = paoType;
    }

    public int getPaoId() {
        return paoId;
    }
    
    public PaoType getPaoType() {
        return paoType;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return this;
    }
    
    @Override
    public String toString() {
        return paoType + ":" + paoId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + paoId;
        result = prime * result + paoType.hashCode();
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
        PaoIdentifier other = (PaoIdentifier) obj;
        if (paoId != other.paoId)
            return false;
        if (!paoType.equals(other.paoType))
            return false;
        return true;
    }
}
