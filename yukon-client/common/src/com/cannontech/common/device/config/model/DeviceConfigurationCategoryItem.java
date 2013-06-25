package com.cannontech.common.device.config.model;

/**
 * Represents a device configuration item in the database.
 */
public final class DeviceConfigurationCategoryItem {
    
    private final Integer categoryId;
    private final String fieldName;
    private final String value;
    
    public DeviceConfigurationCategoryItem(Integer categoryId, String fieldName, String value) {
        this.categoryId = categoryId;
        this.fieldName = fieldName;
        this.value = value;
    }
    
    public Integer getCategoryId() {
        return categoryId;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public String getValue() {
        return value;
    }
}
