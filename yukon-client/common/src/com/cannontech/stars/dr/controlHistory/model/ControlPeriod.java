package com.cannontech.stars.dr.controlHistory.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ControlPeriod implements DisplayableEnum {
    PAST_DAY("PastDay"),
    PAST_WEEK("PastWeek"),
    PAST_MONTH("PastMonth"),
    PAST_YEAR("PastYear"),
    ALL("All");

    private static final String keyPrefix = "yukon.dr.program.displayname.controlPeriod.";
    
    private final String starsName;
    ControlPeriod(final String starsName) {
        this.starsName = starsName;
    }

    public String starsName() {
        return starsName;
    }

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}