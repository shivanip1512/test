package com.cannontech.system.model;

public class ThemeProperty {
    
    private ThemePropertyType type;
    private String value;
    
    public ThemePropertyType getType() {
        return type;
    }

    public void setType(ThemePropertyType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}