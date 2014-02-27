package com.cannontech.dr.rfn.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum UnknownStatus implements DisplayableEnum {

    COMMUNICATING,
    NOT_COMMUNICATING,
    NEW_INSTALL_NOT_COMMUNICATING;

    @Override
    public String getFormatKey() {
        return "yukon.common.rfPerformance.unknownStatus." + name();
    }
}