package com.cannontech.amr.archivedValueExporter.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ArchivedValuesExportTimeZoneFormat implements DisplayableEnum {
    LOCALTZ,
    LOCALTZ_NO_DST,
    UTCTZ;
    
    private final static String keyPrefix = "yukon.web.modules.amr.archivedValueExporter.";
    
    @Override
    public String getFormatKey() {
        return keyPrefix + "tzDisplayOption." + name();
    }
}
