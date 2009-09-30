package com.cannontech.common.device.groups.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DeviceGroupComposedCompositionType implements DisplayableEnum {

    UNION("OR"),
    INTERSECTION("AND");
    
    private String sqlFragmentCombiner;
    
    DeviceGroupComposedCompositionType(String sqlFragmentCombiner) {
        this.sqlFragmentCombiner = sqlFragmentCombiner;
    }
    
    public String getSqlFragmentCombiner() {
        return this.sqlFragmentCombiner;
    }
    
    private final String keyPrefix = "yukon.web.deviceGroups.deviceGroupComposedCompositionType.";
    
    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
