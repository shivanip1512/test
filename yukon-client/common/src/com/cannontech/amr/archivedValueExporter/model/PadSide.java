package com.cannontech.amr.archivedValueExporter.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum PadSide implements DisplayableEnum {
    NONE,
    LEFT,
    RIGHT;

    private final static String keyPrefix = "yukon.web.modules.amr.archivedValueExporter.";

    @Override
    public String getFormatKey() {
        return keyPrefix + "padSide." + name();
    }
}
