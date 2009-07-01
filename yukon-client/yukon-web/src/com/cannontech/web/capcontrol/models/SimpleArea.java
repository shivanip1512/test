package com.cannontech.web.capcontrol.models;

import java.util.List;

public class SimpleArea {
    
    private List<SimpleSubstation> substations;
    private String name;
    private Integer id;
    
    public SimpleArea(List<SimpleSubstation> substations) {
        this.substations = substations;
    }
    
    public List<SimpleSubstation> getSubstations(){
        return substations;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
