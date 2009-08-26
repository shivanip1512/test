package com.cannontech.common.pao;


public final class PaoIdentifier implements YukonPao {

    private int paoId;
    private PaoType paoType;

    public PaoIdentifier(int paoId, PaoType paoType) {
        super();
        this.paoId = paoId;
        this.paoType = paoType;
        if (paoType == null) {
            System.out.println("null paoType");
            new Throwable().printStackTrace();
        }
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
