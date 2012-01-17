package com.cannontech.common.pao.attribute.model;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;


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

    @Override
    public MessageSourceResolvable getMessage() {
        return YukonMessageSourceResolvable.createDefaultWithoutCode(description);
    }
}
