package com.cannontech.web.stars.dr.operator.inventory.configuration.model;

/**
 * Used as select options on the 'send new config' page. 
 * Options are either addressing groups or static load group mapping groups based
 * on if static load group mappings exist in the database.
 */
public class Group {
    
    private int id;
    private String name;
    
    private Group(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public static Group of(int id, String name) {
        return new Group(id, name);
    }
    
}