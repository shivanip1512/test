package com.cannontech.stars.dr.displayable.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ProgramStatus implements DisplayableEnum {
    IN_SERVICE,
    OUT_OF_SERVICE;

    private final static String keyPrefix = "yukon.dr.programStatus.";

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
