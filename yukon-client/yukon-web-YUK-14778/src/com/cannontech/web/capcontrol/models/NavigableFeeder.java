package com.cannontech.web.capcontrol.models;

import java.util.List;

public class NavigableFeeder {
    
    private List<NavigableCapBank> capbanks;
    private String name;
    private Integer id;
    
    public NavigableFeeder(List<NavigableCapBank> capbanks) {
        this.capbanks = capbanks;
    }
    
    public List<NavigableCapBank> getCapbanks(){
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
