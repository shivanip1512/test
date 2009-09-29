package com.cannontech.common.model;

import com.cannontech.common.model.Route;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;

public class Route implements DisplayablePao{
    private int id;
    private String name;
    private int order;
    private PaoType paoType;
    
    public Route() { }
    
    public Route(final int id, final String name, final int order, PaoType paoType) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.paoType = paoType;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }

    public void setName(final String name) {
        this.name = name;
    }
    
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
        return ((this.id == obj.id) &&
                (this.name.equals(obj.name)) &&
                (this.order == obj.order));
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = result * 37 + id;
        result = result * 37 + name.length();
        result = result * 37 + order;
        return result;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return new PaoIdentifier(id, paoType);
    }

    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }

    public PaoType getPaoType() {
        return paoType;
    }
    
}
