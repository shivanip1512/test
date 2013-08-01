package com.cannontech.common.device.config.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class DisplayableConfigurationCategory {
    private final int categoryId;
    private final String categoryName;
    private final String categoryType;
    private final List<String> configNames;
    
    public DisplayableConfigurationCategory(int categoryId, String categoryName, String categoryType, 
                                            List<String> configNames) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.configNames = configNames;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public String getCategoryType() {
        return categoryType;
    }
    
    public List<String> getConfigNames() {
        return ImmutableList.copyOf(configNames);
    }
}
