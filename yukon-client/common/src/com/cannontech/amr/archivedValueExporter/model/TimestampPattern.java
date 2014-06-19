package com.cannontech.amr.archivedValueExporter.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum TimestampPattern implements DisplayableEnum {
    
    MONTH_DAY_YEAR("MM/dd/yyyy"),
    DAY_MONTH_YEAR("dd/MM/yyyy"),
    HOUR_MINUTE_SECOND_12("hh:mm:ss a"),
    HOUR_MINUTE_SECOND_24("HH:mm:ss"),
    CUSTOM("CUSTOM");
    
    private final String pattern;
    
    private TimestampPattern(String pattern) {
        this.pattern = pattern;
    }
    
    public String getPattern() {
        return pattern;
    }
    
    public boolean isCustom() {
        return this == CUSTOM;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.bulk.archivedValueExporter.timestampPattern." + name();
    }
}