package com.cannontech.amr.deviceDataMonitor.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ProcessorType implements DisplayableEnum {
    STATE,
    RANGE,
    LESS,
    GREATER;
    
    public boolean isStateBased() {
        return this == STATE;
    }
    
    public boolean isValueBased() {
        return this != STATE;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.amr.deviceDataMonitor.processorType." + this;
    }
}
