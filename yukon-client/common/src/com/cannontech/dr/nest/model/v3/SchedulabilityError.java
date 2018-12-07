package com.cannontech.dr.nest.model.v3;

import com.cannontech.common.i18n.DisplayableEnum;

public enum SchedulabilityError implements DisplayableEnum {
    SCHEDULABILITY_ERROR_UNSPECIFIED,
    OUTSIDE_DAILY_TIME_BOUNDS,
    INVALID_DURATION,
    WEEKEND_EVENTS_NOT_ALLOWED,
    EVENT_REQUEST_TOO_LATE,
    EVENT_REQUEST_TOO_EARLY,
    TOO_MANY_EVENTS_SAME_DAY,
    TOO_MANY_EVENTS_SAME_WEEK,
    TOO_MANY_EVENTS_SAME_SEASON,
    LOAD_SHAPING_OPTION_NOT_ALLOWED
    ;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.nest.schedulabilityError." + name();

    }  
}
