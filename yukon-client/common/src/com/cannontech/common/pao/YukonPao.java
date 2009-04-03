package com.cannontech.common.pao;

import java.io.Serializable;

import org.springframework.core.style.ToStringCreator;

public class YukonPao implements Cloneable, Serializable {
    private int paoId;
    private int type;

    public YukonPao(int paoId, int type) {
        super();
        this.paoId = paoId;
        this.type = type;
    }

    public YukonPao() {
    }

    public int getPaoId() {
        return paoId;
    }

    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("paoId", getPaoId());
        tsc.append("type", getType());
        return tsc.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + paoId;
        result = prime * result + type;
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
        YukonPao other = (YukonPao) obj;
        if (paoId != other.paoId)
            return false;
        if (type != other.type)
            return false;
        return true;
    }


}
