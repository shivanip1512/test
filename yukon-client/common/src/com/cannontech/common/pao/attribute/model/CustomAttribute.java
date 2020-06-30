package com.cannontech.common.pao.attribute.model;

public class CustomAttribute {
    private int id;
    private String name;
    private String key = "yukon.common.attribute.customAttribute.";
        
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getKey() {
        return key + id;
    }
}
