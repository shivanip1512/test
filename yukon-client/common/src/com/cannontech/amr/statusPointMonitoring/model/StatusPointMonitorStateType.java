package com.cannontech.amr.statusPointMonitoring.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum StatusPointMonitorStateType implements DisplayableEnum {
    
    DONT_CARE,
    DIFFERENCE,
    EXACT,
    ;
    
    private final String keyPrefix = "yukon.web.modules.amr.statusPointMonitor.state.";
    
    public boolean isDontCare() {
        return this == DONT_CARE;
    }
    
    public boolean isDifference() {
        return this == DIFFERENCE;
    }
    
    public boolean isExact() {
        return this == EXACT;
    }

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}