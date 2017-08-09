package com.cannontech.common.pao.definition.dao;

import com.cannontech.common.pao.definition.loader.jaxb.CategoryType;
import com.cannontech.common.pao.definition.loader.jaxb.DeviceCategories.Category;

public class ConfigurationCategory {
    private CategoryType type;
    private boolean optional;
    
    private ConfigurationCategory(CategoryType type, boolean optional) {
        this.type     = type;
        this.optional = optional;
    }
    
    public static ConfigurationCategory of(Category c) {
        return new ConfigurationCategory(c.getType(), Boolean.TRUE.equals(c.isOptional()));
    }
    
    public CategoryType getType() {
        return type;
    }
    
    public boolean isOptional() {
        return optional;
    }
    
    public boolean isRequired() {
        return !optional;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (optional ? 1231 : 1237);
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConfigurationCategory other = (ConfigurationCategory) obj;
        if (optional != other.optional)
            return false;
        if (type != other.type)
            return false;
        return true;
    }
}
