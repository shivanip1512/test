package com.cannontech.web.capcontrol.models;

import java.util.List;

public class SimpleSubstation {
    
    private List<SimpleSubstationBus> substationBuses;
    private String name;
    private Integer id;
    
    public SimpleSubstation(List<SimpleSubstationBus> substationBuses) {
        this.substationBuses = substationBuses;
    }
    
    public List<SimpleSubstationBus> getSubstationBuses(){
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
