package com.cannontech.amr.archivedValueExporter.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum AttributeField implements DisplayableEnum {
    TIMESTAMP,
    VALUE,
    POINT_STATE,
    UNIT_OF_MEASURE,
    QUALITY;
    
    private final static String keyPrefix = "yukon.web.modules.tools.bulk.archivedValueExporter.";
    
    @Override
    public String getFormatKey() {
        return keyPrefix + "attributeField." + name();
    }
}
