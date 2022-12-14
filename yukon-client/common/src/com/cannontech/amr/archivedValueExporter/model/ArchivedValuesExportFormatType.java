package com.cannontech.amr.archivedValueExporter.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ArchivedValuesExportFormatType implements DisplayableEnum {
    
    FIXED_ATTRIBUTE,
    DYNAMIC_ATTRIBUTE;

    private final static String baseKey =  "yukon.web.modules.tools.bulk.archivedValueExporter.formatType.";
    
    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
    
    public String getDescription() {
        return baseKey + name() + ".description";
    }
    
}