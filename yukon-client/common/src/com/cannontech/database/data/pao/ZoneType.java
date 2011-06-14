package com.cannontech.database.data.pao;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ZoneType implements DisplayableEnum {
    GANG_OPERATED,
    THREE_PHASE,
    SINGLE_PHASE,
    ;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.capcontrol.ivvc.zone." + name();
    }
}
