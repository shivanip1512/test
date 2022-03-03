package com.cannontech.amr.archivedValueExporter.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DataSelection implements DisplayableEnum {
    LATEST,
    MAX,
    MIN,
    EARLIEST,
    SUM,
    ;
    
    private final static String keyPrefix = "yukon.web.modules.tools.bulk.archivedValueExporter.dataSelection.";
    
    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
