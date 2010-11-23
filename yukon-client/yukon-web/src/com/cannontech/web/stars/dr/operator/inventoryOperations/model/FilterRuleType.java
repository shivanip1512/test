package com.cannontech.web.stars.dr.operator.inventoryOperations.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum FilterRuleType implements DisplayableEnum {
    LOAD_GROUP,
    PROGRAM,
    FIELD_INSTALL_DATE,
    PROGRAM_SIGNUP_DATE,
    DEVICE_TYPE,
    SERIAL_NUMBER_RANGE,
    UNENROLLED;
    
    public String getValue() {
        return name();
    }
    
    public FilterRuleType getDisplayName() {
        return this;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.operator.filterSelection.filterRuleType." + name();
    }
}