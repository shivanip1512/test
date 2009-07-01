package com.cannontech.web.capcontrol.models;

import java.util.List;

public class SimpleFeeder {
    
    private List<SimpleCapBank> capbanks;
    private String name;
    private Integer id;
    
    public SimpleFeeder(List<SimpleCapBank> capbanks) {
        this.capbanks = capbanks;
    }
    
    public List<SimpleCapBank> getCapbanks(){
        return capbanks;
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
