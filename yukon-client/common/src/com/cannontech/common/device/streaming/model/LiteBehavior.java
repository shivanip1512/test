package com.cannontech.common.device.streaming.model;

public class LiteBehavior {
    private int id;
    private String name;
   
    public LiteBehavior(int id, String name){
        this.id = id;
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
    public int getId() {
        return id;
    }

}
