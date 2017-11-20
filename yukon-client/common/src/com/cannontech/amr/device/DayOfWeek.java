package com.cannontech.amr.device;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DayOfWeek implements DisplayableEnum {
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY;

    @Override
    public String getFormatKey() {
        return "yukon.common.day." + name() + ".short";
    }

}