package com.cannontech.amr.archivedValueExporter.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum MissingAttribute implements DisplayableEnum {
    LEAVE_BLANK,
    SKIP_RECORD,
    FIXED_VALUE;

    private final static String keyPrefix = "yukon.web.modules.amr.archivedValueExporter.";

    @Override
    public String getFormatKey() {
        return keyPrefix + "missingAttribute." + name();
    }

}
