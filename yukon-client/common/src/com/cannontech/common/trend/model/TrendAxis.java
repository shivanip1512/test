package com.cannontech.common.trend.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum TrendAxis implements DisplayableEnum {
    LEFT, RIGHT;

    private String baseKey = "yukon.web.modules.tools.trend.axis.";

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}
