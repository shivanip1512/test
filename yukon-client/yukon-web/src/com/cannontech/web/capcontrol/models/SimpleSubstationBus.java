package com.cannontech.web.capcontrol.models;

import java.util.List;

public class SimpleSubstationBus {
    
    private List<SimpleFeeder> feeders;
    private String name;
    private Integer id;
    
    public SimpleSubstationBus(List<SimpleFeeder> feeders) {
        this.feeders = feeders;
    }
    
    public List<SimpleFeeder> getFeeders(){
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
