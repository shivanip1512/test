package com.cannontech.web.capcontrol.models;

import java.util.List;

public class NavigableArea {
    
    private List<NavigableSubstation> substations;
    private String name;
    private Integer id;
    
    public NavigableArea(List<NavigableSubstation> substations) {
        this.substations = substations;
    }
    
    public List<NavigableSubstation> getSubstations(){
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
