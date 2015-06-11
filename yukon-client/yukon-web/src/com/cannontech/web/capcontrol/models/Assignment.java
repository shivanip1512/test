
package com.cannontech.web.capcontrol.models;

public final class Assignment {
    
    private int id;
    private String name;
    
    private Assignment(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public static Assignment of(int id, String name) {
        return new Assignment(id, name);
    }
    
    @Override
    public String toString() {
        return String.format("Assignment [id=%s, name=%s]", id, name);
    }
    
}