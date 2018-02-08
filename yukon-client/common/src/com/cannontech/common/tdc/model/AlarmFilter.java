package com.cannontech.common.tdc.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum AlarmFilter implements DisplayableEnum {
    NO_FILTER(""),
    UNACKNOWLEDGED_ALARMS(""),
    ACTIVE_ALARMS(""),
    ALARM_HISTORY(""),
    ;
    
    private String key;
    
    AlarmFilter(String key) {
        this.setKey(key);
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.tdc.alarmFilters." + name();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
}
