package com.cannontech.common.pao.attribute.model;


public class UserDefinedAttribute implements Attribute {
    private String key;
    private String description = "";
    
    public UserDefinedAttribute(String key) {
        this.key = key;
    }
    
    public String getDescription() {
        return description;
    }

    public String getKey() {
        return key;
    }

}
