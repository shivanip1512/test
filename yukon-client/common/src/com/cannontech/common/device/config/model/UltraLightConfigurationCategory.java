package com.cannontech.common.device.config.model;

public class UltraLightConfigurationCategory {
    private final int categoryId;
    private final String categoryType;
    private final String name;
    
    public UltraLightConfigurationCategory(int categoryId, String categoryType, String name) {
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
