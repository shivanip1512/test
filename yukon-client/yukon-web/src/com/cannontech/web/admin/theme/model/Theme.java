package com.cannontech.web.admin.theme.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Theme {

    private Integer themeId;
    private String name;
    private boolean editable = true;
    private boolean currentTheme = false;
    private Map<ThemePropertyType, Object> properties = new LinkedHashMap<>();
    
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
    
    public boolean isEditable() {
        return editable;
    }
    
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    public boolean isCurrentTheme() {
        return currentTheme;
    }
    
    public void setCurrentTheme(boolean currentTheme) {
        this.currentTheme = currentTheme;
    }
    
    public Map<ThemePropertyType, Object> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<ThemePropertyType, Object> properties) {
        this.properties = properties;
    }
    
}