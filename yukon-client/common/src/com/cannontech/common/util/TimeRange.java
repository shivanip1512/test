package com.cannontech.common.util;

import com.cannontech.common.i18n.DisplayableEnum;

public enum TimeRange implements DisplayableEnum {
    
    HR_1(1),
    HR_6(6),
    HR_12(12),
    DAY_1(24),
    DAY_2(48),
    WEEK_1(168),
    ;
    
    private int hours;
    private TimeRange(int hours) {
        this.hours = hours;
    }
    
    public int getHours() {
        return hours;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.common.time.range." + name();
    }
    
}