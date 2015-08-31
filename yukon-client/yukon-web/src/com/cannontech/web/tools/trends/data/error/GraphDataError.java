package com.cannontech.web.tools.trends.data.error;
import com.cannontech.common.i18n.DisplayableEnum;

public enum GraphDataError implements DisplayableEnum {
    NO_TREND_DATA_AVAILABLE, DB_ERROR, PARSE_ERROR;

    /**
     * getFormatKey
     * retrieves "this" instance of the {@link GraphDataError} name i.e. class name and delivers it as the entire namespace.
     * @return {@link String}
     */
    @Override public String getFormatKey() {
        return "yukon.web.modules.tools.trends.data.error.graphDataError." + name();
    }
}
