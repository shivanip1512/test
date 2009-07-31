package com.cannontech.common.pao;


public final class PaoIdentifier implements YukonPao {

    private int paoId;
    private PaoType paoType;
    private PaoCategory paoCategory;

    public PaoIdentifier(int paoId, PaoType paoType, PaoCategory paoCategory) {
        super();
        this.paoId = paoId;
        this.paoType = paoType;
        this.paoCategory = paoCategory;
    }

    public int getPaoId() {
        return paoId;
    }
    public PaoType getPaoType() {
        return paoType;
    }
    public PaoCategory getPaoCategory() {
        return paoCategory;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return this;
    }
    
    @Override
    public String toString() {
        return paoCategory + ":" + paoType + ":" + paoId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((paoCategory == null) ? 0
                : paoCategory.hashCode());
        result = prime * result + paoId;
        result = prime * result + ((paoType == null) ? 0 : paoType.hashCode());
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
        if (paoCategory == null) {
            if (other.paoCategory != null)
                return false;
        } else if (!paoCategory.equals(other.paoCategory))
            return false;
        if (paoId != other.paoId)
            return false;
        if (paoType == null) {
            if (other.paoType != null)
                return false;
        } else if (!paoType.equals(other.paoType))
            return false;
        return true;
    }


}
