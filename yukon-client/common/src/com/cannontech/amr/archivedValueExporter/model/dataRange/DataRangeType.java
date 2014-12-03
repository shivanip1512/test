package com.cannontech.amr.archivedValueExporter.model.dataRange;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DataRangeType implements DisplayableEnum {
    DATE_RANGE,
    DAYS_PREVIOUS,
    END_DATE,
    SINCE_LAST_CHANGE_ID,
    DAYS_OFFSET
    ;

    private static final String keyPrefix = "yukon.web.modules.tools.bulk.archivedValueExporter.dataRangeType.";

    @Override
    public String getFormatKey() {
        return keyPrefix+name();
    }
}