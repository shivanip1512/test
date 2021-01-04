package com.cannontech.amr.archivedValueExporter.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum FieldValue implements DisplayableEnum {
    DEFAULT(FieldType.ATTRIBUTE_NAME),
    CMEP(FieldType.ATTRIBUTE_NAME);

    private final static String keyPrefix = "yukon.web.modules.tools.bulk.archivedValueExporter.";

    private FieldType[] fieldTypes;

    FieldValue(FieldType... fieldTypes) {
        this.fieldTypes = fieldTypes;
    }

    public FieldType[] getFieldTypes() {
        return fieldTypes;
    }

    @Override
    public String getFormatKey() {
        return keyPrefix + "fieldValue." + name();
    }

}
