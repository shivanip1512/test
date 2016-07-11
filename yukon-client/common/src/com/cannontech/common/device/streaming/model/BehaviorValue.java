package com.cannontech.common.device.streaming.model;

public class BehaviorValue {
    private String name;
    private String value;
    
    public BehaviorValue(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    public String getName() {
        return name;
    }
}
