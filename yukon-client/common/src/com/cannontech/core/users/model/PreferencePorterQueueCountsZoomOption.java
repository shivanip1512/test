package com.cannontech.core.users.model;

import com.cannontech.common.i18n.DisplayableEnum;

/*
 * This enum is being used by yukon.widget.porterQueueCounts.js. Any change in order would
 * affect the current functionality of preferred zoom option selected by the user.
 */
public enum PreferencePorterQueueCountsZoomOption implements DisplayableEnum {
    DAY_1, 
    WEEK_1, 
    MONTH_1, 
    MONTH_3;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.user.preferences." + getParentName() + "." + name();
    }

    public static String getParentName() {
        return "PORTER_QUEUE_COUNTS_ZOOM";
    }

    public static PreferencePorterQueueCountsZoomOption getDefault() {
        return WEEK_1;
    }
}
