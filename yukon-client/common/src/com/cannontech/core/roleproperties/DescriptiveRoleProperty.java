package com.cannontech.core.roleproperties;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;

public class DescriptiveRoleProperty {
    private YukonRoleProperty yukonRoleProperty;
    private MessageSourceResolvable key;
    private MessageSourceResolvable description;
    private Object defaultValue;
    
    
    /**
     * Creates a normal DescriptiveRoleProperty.
     */
    public DescriptiveRoleProperty(YukonRoleProperty yukonRoleProperty, Object defaultValue,
            MessageSourceResolvable key, MessageSourceResolvable description) {
        super();
        this.yukonRoleProperty = yukonRoleProperty;
        this.defaultValue = defaultValue;
        this.key = key;
        this.description = description;
    }
    
    /**
     * Creates a "default" DescriptiveRoleProperty for a YukonRoleProperty that doesn't exist
     * in the database.
     */
    public DescriptiveRoleProperty(YukonRoleProperty yukonRoleProperty) {
        super();
        this.yukonRoleProperty = yukonRoleProperty;
        this.defaultValue = null;
        this.key = YukonMessageSourceResolvable.createDefaultWithoutCode(yukonRoleProperty.name());
        this.description = YukonMessageSourceResolvable.createDefaultWithoutCode(yukonRoleProperty.name());
    }
    public YukonRoleProperty getYukonRoleProperty() {
        return yukonRoleProperty;
    }
    public MessageSourceResolvable getKey() {
        return key;
    }
    public MessageSourceResolvable getDescription() {
        return description;
    }
    public Object getDefaultValue() {
        return defaultValue;
    }
}
