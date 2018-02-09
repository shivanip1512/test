package com.cannontech.common.tdc.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum AlarmFilter implements DisplayableEnum {
    NO_FILTER,
    UNACKNOWLEDGED_ALARMS,
    ACTIVE_ALARMS,
    ALARM_HISTORY,
    ;
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.tdc.alarmFilters." + name();
    }
    
}
