package com.cannontech.web.amr.waterLeakReport.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum SortBy implements DisplayableEnum {
    DEVICE_NAME,
    METER_NUMBER,
    PAO_TYPE,
    LEAK_RATE,
    USAGE,
    DATE;
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.amr.waterLeakReport.report.tableHeader." + name();
    }
}