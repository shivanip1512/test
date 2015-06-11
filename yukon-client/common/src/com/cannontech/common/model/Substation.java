package com.cannontech.common.model;

import com.cannontech.common.model.Substation;

public class Substation implements Comparable<Substation> {
    
    private int id;
    private String name;
    
    public Substation() { } 
    
    public Substation(final int id, final String name) {
        this.id = id;
        this.name = name;
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
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Substation other = (Substation) obj;
        if (id != other.id)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public int compareTo(Substation substation) {
        return name.compareToIgnoreCase(substation.getName());
    }
    
    @Override
    public String toString() {
        return String.format("Substation [id=%s, name=%s]", id, name);
    }
    
}