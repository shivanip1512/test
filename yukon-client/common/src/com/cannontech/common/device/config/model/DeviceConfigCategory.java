package com.cannontech.common.device.config.model;

import java.util.List;

/**
 * Represents a device configuration category in the database.
 */
public final class DeviceConfigCategory {
    private final Integer categoryId;
    private final String categoryType;
    private final String categoryName;
    private final List<DeviceConfigCategoryItem> deviceConfigurationItems;
    
    public DeviceConfigCategory(Integer categoryId, 
                                String categoryType, 
                                String categoryName, 
                                List<DeviceConfigCategoryItem> deviceConfigurationItems) {
        this.categoryId = categoryId;
        this.categoryType = categoryType;
        this.categoryName = categoryName;
        this.deviceConfigurationItems = deviceConfigurationItems;
    }
    
    public Integer getCategoryId() {
        return categoryId;
    }
    
    public String getCategoryType() {
        return categoryType;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public List<DeviceConfigCategoryItem> getDeviceConfigurationItems() {
        return deviceConfigurationItems;
    }
}
