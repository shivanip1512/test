package com.cannontech.support.service.impl;

import org.joda.time.Months;
import org.joda.time.ReadablePeriod;
import org.joda.time.Weeks;
import org.joda.time.Years;

import com.cannontech.common.i18n.DisplayableEnum;

public enum BundleRangeSelection implements DisplayableEnum {
    ONE_WEEK(Weeks.ONE),
    TWO_WEEKS(Weeks.TWO),
    ONE_MONTH(Months.ONE),
    EVERYTHING(Years.years(100));

    private static final String keyPrefix =
        "yukon.web.modules.support.supportBundle.bundleRangeSelection.";

    final private ReadablePeriod duration;
    
    BundleRangeSelection(ReadablePeriod duration) {
        this.duration = duration;
    }
    
    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
    
    public ReadablePeriod getDuration() {
        return duration;
    }
}
