
package com.cannontech.web.tools.trends.data;

import com.cannontech.common.i18n.DisplayableEnum;

public enum GraphDataState implements DisplayableEnum{
    DATA_AVAILABLE,
    NO_TREND_DATA_AVAILABLE,
    DB_ERROR,
    PARSE_ERROR;

    @Override
    public String getFormatKey() {
        // TODO Auto-generated method stub
        return "yukon.web.modules.tools.trend.GraphDataState." + name();
    }
}

