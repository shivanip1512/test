package com.cannontech.amr.archivedValueExporter.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum FieldValue implements DisplayableEnum {
    DEFAULT,
    CMEP;

    private final static String keyPrefix = "yukon.web.modules.tools.bulk.archivedValueExporter.";

    @Override
    public String getFormatKey() {
        return keyPrefix + "fieldValue." + name();
    }

}
