package com.cannontech.common.model;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;

public class Route implements DisplayablePao {
    private PaoIdentifier paoIdentifier;
    private String name;
    private int order;
    
    public Route(final int id, final String name, final int order, PaoType paoType) {
        paoIdentifier = new PaoIdentifier(id,  paoType);
        this.name = name;
        this.order = order;
    }
    
    public int getId() {
        return paoIdentifier.getPaoId();
    }

    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }

    public void setOrder(final int order) {
        this.order = order;
    }
    
    public int getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Route other = (Route) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (order != other.order)
            return false;
        if (paoIdentifier == null) {
            if (other.paoIdentifier != null)
                return false;
        } else if (!paoIdentifier.equals(other.paoIdentifier))
            return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + order;
        result = prime * result + ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
        return result;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }

    public PaoType getPaoType() {
        return paoIdentifier.getPaoType();
    }
}
