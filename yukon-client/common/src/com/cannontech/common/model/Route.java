package com.cannontech.common.model;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;

public class Route implements DisplayablePao {
    private PaoIdentifier paoIdentifier;
    private String name;
    private int order;
    
    public Route() { }
    
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
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Route)) return false;
        Route obj = (Route) o;
        return ((this.paoIdentifier == obj.paoIdentifier) &&
                (this.name.equals(obj.name)) &&
                (this.order == obj.order));
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = result * 37 + paoIdentifier.getPaoId();
        result = result * 37 + name.length();
        result = result * 37 + order;
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
