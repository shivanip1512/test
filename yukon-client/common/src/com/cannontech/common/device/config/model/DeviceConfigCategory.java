package com.cannontech.common.device.config.model;

import java.util.List;

/**
 * Represents a device configuration category in the database.
 */
public final class DeviceConfigCategory {
    private final Integer categoryId;
    private final String categoryType;
    private final String categoryName;
    private final String description;
    private final List<DeviceConfigCategoryItem> deviceConfigurationItems;
    
    public DeviceConfigCategory(Integer categoryId, 
                                String categoryType, 
                                String categoryName, 
                                String description,
                                List<DeviceConfigCategoryItem> deviceConfigurationItems) {
        this.categoryId = categoryId;
        this.categoryType = categoryType;
        this.categoryName = categoryName;
        this.description = description;
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
    
    public String getDescription() {
        return description;
    }
    
    public List<DeviceConfigCategoryItem> getDeviceConfigurationItems() {
        return deviceConfigurationItems;
    }

    @Override
    public String toString() {
        return String.format(
            "DeviceConfigCategory [categoryId=%s, categoryType=%s, categoryName=%s, description=%s, deviceConfigurationItems=%s]",
            categoryId, categoryType, categoryName, description, deviceConfigurationItems);
    }

}
