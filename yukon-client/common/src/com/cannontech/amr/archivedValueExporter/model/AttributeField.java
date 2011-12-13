package com.cannontech.amr.archivedValueExporter.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum AttributeField implements DisplayableEnum {
    TIMESTAMP,
    VALUE,
    U_OF_M;
    
    private final static String keyPrefix = "yukon.web.modules.amr.archivedValueExporter.";
    
    @Override
    public String getFormatKey() {
        return keyPrefix + "attributeField." + name();
    }

    public String getKey() {
        return name();
    }
}
