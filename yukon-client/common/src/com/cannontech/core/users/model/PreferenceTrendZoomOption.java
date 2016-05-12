package com.cannontech.core.users.model;

import com.cannontech.common.i18n.DisplayableEnum;

/*
 * The PreferenceTrendZoomOption enum is being used by yukon.tools.trends.js hence any change in order would
 * affect the current functionality of preferred zoom option selected by the user.
 */
public enum PreferenceTrendZoomOption implements DisplayableEnum {
    DAY_1, 
    WEEK_1, 
    MONTH_1, 
    MONTH_3, 
    MONTH_6, 
    YTD, 
    YEAR_1, 
    ALL;

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

}