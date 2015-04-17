package com.cannontech.common.pao;

import static com.google.common.base.Preconditions.*;

import java.io.Serializable;
import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

public final class PaoIdentifier implements YukonPao, Serializable {
    
    private static final long serialVersionUID = 1L;
    public static final Comparator<PaoIdentifier> COMPARATOR;
    
    private final int paoId;
    private final PaoType paoType;
    
    public PaoIdentifier(int paoId, PaoType paoType) {
        checkNotNull(paoType, "paoType must not be null");
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
    @JsonIgnore
    public PaoIdentifier getPaoIdentifier() {
        return this;
    }
    
    @Override
    public String toString() {
        return String.format("PaoIdentifier [paoId=%s, paoType=%s]", paoId, paoType);
    }
    
    static {
        Ordering<Integer> intComparer = Ordering.natural();
        Ordering<PaoIdentifier> paoIdOrdering = intComparer.onResultOf(new Function<PaoIdentifier, Integer>() {
            @Override
            public Integer apply(PaoIdentifier from) {
                return from.getPaoId();
            }
        });
        Ordering<PaoType> paoTypeComparer = Ordering.natural();
        Ordering<PaoIdentifier> paoTypeOrdering = paoTypeComparer.onResultOf(new Function<PaoIdentifier, PaoType>() {
            @Override
            public PaoType apply(PaoIdentifier from) {
                return from.getPaoType();
            }
        });
        COMPARATOR = paoIdOrdering.compound(paoTypeOrdering);
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PaoIdentifier other = (PaoIdentifier) obj;
        if (paoId != other.paoId) {
            return false;
        }
        if (!paoType.equals(other.paoType)) {
            return false;
        }
        return true;
    }
    
    public static PaoIdentifier of (int paoId, PaoType type) {
        return new PaoIdentifier(paoId, type);
    }
    
}