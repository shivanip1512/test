package com.cannontech.common.device.config.model;

public class LightConfigurationCategory {
    private final int categoryId;
    private final String categoryType;
    private final String name;
    
    public LightConfigurationCategory(int categoryId, String categoryType, String name) {
        this.categoryId = categoryId;
        this.categoryType = categoryType;
        this.name = name;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public String getCategoryType() {
        return categoryType;
    }
    
    public String getName() {
        return name;
    }
}
