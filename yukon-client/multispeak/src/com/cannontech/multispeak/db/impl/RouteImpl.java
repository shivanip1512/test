package com.cannontech.multispeak.db.impl;

import com.cannontech.multispeak.db.Route;

public class RouteImpl implements Route {
    private int id;
    private String name;
    private int order;
    
    public RouteImpl() { }
    
    public RouteImpl(final int id, final String name, final int order) {
        this.id = id;
        this.name = name;
        this.order = order;
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
        if (o == null || !(o instanceof RouteImpl)) return false;
        RouteImpl obj = (RouteImpl) o;
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
    
}
