package com.cannontech.web.deviceConfiguration.model;

import java.util.List;

import com.cannontech.common.device.config.model.jaxb.CategoryType;

/**
 * Simple model object used to render categories and their fields in the web view.
 */
public class CategoryTemplate {
    private final String name;
    private final CategoryType categoryType;
    private final List<Field<?>> fields;

    public CategoryTemplate(String name, CategoryType categoryType, List<Field<?>> fields) {
        this.name = name;
        this.categoryType = categoryType;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public String getCategoryType() {
        return categoryType.value();
    }
    
    public List<Field<?>> getFields() {
        return fields;
    }
}
