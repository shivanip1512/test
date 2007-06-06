package com.cannontech.multispeak.db;

import com.cannontech.multispeak.db.Substation;

public class Substation {
    private int id;
    private String name;
    private int routeId;
    
    public Substation() { } 
    
    public Substation(final int id, final String name, final int routeId) {
        this.id = id;
        this.name = name;
        this.routeId = routeId;
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
    
    public void setRouteId(final int routeId) {
        this.routeId = routeId;
    }
    
    public int getRouteId() {
        return routeId;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Substation)) return false;
        Substation obj = (Substation) o;
        return ((this.id == obj.id) &&
                (this.name.equals(obj.name)) &&
                (this.routeId == obj.routeId));
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = result * 37 + id;
        result = result * 37 + name.length();
        result = result * 37 + routeId;
        return result;
    }
}
