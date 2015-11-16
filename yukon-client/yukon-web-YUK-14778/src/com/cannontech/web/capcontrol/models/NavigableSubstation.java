package com.cannontech.web.capcontrol.models;

import java.util.List;

public class NavigableSubstation {
    
    private List<NavigableSubstationBus> substationBuses;
    private String name;
    private Integer id;
    
    public NavigableSubstation(List<NavigableSubstationBus> substationBuses) {
        this.substationBuses = substationBuses;
    }
    
    public List<NavigableSubstationBus> getSubstationBuses(){
        return substationBuses;
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
