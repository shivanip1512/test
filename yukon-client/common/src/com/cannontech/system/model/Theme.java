package com.cannontech.system.model;

import java.util.List;

public class Theme {

    private Integer themeId;
    private String name;
    private List<ThemeProperty> properties;
    
    public Integer getThemeId() {
        return themeId;
    }
    
    public void setThemeId(Integer themeId) {
        this.themeId = themeId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<ThemeProperty> getProperties() {
        return properties;
    }
    
    public void setProperties(List<ThemeProperty> properties) {
        this.properties = properties;
    }
    
}