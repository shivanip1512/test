package com.cannontech.web.stars.dr.operator.inventoryOperations.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum FilterRuleType implements DisplayableEnum {
    APPLIANCE_TYPE,
    CUSTOMER_TYPE,
    DEVICE_STATUS,
    DEVICE_STATUS_DATE_RANGE,
    DEVICE_TYPE,
    FIELD_INSTALL_DATE,
    LOAD_GROUP,
    MEMBER,
    POSTAL_CODE,
    PROGRAM,
    PROGRAM_SIGNUP_DATE,
    SERIAL_NUMBER_RANGE,
    SERVICE_COMPANY,
    UNENROLLED,
    WAREHOUSE,
    ;
    
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