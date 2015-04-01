package com.cannontech.common.model;

import com.cannontech.common.i18n.DisplayableEnum;

/** General purpose status enumeration */
public enum Status implements DisplayableEnum {
    
    UNSUPPORTED,
    SUCCESSFUL,
    FAILED;
    
    @Override
    public String getFormatKey() {
        return "yukon.common." + name().toLowerCase();
    }
    
}