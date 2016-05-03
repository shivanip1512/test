package com.cannontech.core.users.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum PreferenceTrendZoomOption implements DisplayableEnum {
    DAY_1(0), 
    WEEK_1(1), 
    MONTH_1(2), 
    MONTH_3(3), 
    MONTH_6(4), 
    YTD(5), 
    YEAR_1(6), 
    ALL(7);

    final private Integer zoomPeriod;

    private PreferenceTrendZoomOption(Integer period) {
        this.zoomPeriod = period;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.user.preferences." + getParentName() + "." + name();
    }

    public static String getParentName() {
        return "TREND_ZOOM";
    }

    public static PreferenceTrendZoomOption getDefault() {
        return MONTH_3;
    }

    public Integer getZoomPeriod() {
        return zoomPeriod;
    }

    /**
     * Returns null if period is null or NOPERIOD.
     */
    public static PreferenceTrendZoomOption fromZoomPeriod(Integer period) {
        switch (period) {
        case 0:
            return DAY_1;
        case 1:
            return WEEK_1;
        case 2:
            return MONTH_1;
        case 3:
            return MONTH_3;
        case 4:
            return MONTH_6;
        case 5:
            return YTD;
        case 6:
            return YEAR_1;
        case 7:
            return ALL;
        default:
            return null;
        }
    }
}