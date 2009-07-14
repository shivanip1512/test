package com.cannontech.web.capcontrol.models;

import java.util.List;

public class NavigableSubstationBus {
    
    private List<NavigableFeeder> feeders;
    private String name;
    private Integer id;
    
    public NavigableSubstationBus(List<NavigableFeeder> feeders) {
        this.feeders = feeders;
    }
    
    public List<NavigableFeeder> getFeeders(){
        return feeders;
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
