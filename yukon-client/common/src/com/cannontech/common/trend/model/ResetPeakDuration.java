package com.cannontech.common.trend.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ResetPeakDuration implements DisplayableEnum {
    TODAY,
    FIRST_DATE_OF_MONTH,
    FIRST_DATE_OF_YEAR,
    SELECTED_DATE;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.trend.resetPeak.duration." + name();
    }

}