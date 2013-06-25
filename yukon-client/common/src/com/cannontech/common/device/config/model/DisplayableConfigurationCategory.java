package com.cannontech.common.device.config.model;

public class DisplayableConfigurationCategory {
    private final int categoryId;
    private final String categoryName;
    private final String categoryType;
    private final int numConfigurations;
    
    public DisplayableConfigurationCategory(int categoryId, String categoryName, String categoryType, 
                                            int numConfigurations) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.numConfigurations = numConfigurations;
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
    
    public int getNumConfigurations() {
        return numConfigurations;
    }
}
