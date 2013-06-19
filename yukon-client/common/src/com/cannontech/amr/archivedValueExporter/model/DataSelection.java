package com.cannontech.amr.archivedValueExporter.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DataSelection implements DisplayableEnum {
    LATEST,
    MAX,
    MIN,
    EARLIEST;
    
    private final static String keyPrefix = "yukon.web.modules.tools.bulk.archivedValueExporter.";
    
    @Override
    public String getFormatKey() {
        return keyPrefix + "dataSelection." + name();
    }
}
