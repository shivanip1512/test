package com.cannontech.multispeak.db;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.base.CaseFormat;

public enum MspLmMappingColumn implements DisplayableEnum {
    STRATEGY,
    SUBSTATION,
    PAO;

    private static final String baseKey = "yukon.web.modules.adminSetup.lmMappings.";

    @Override
    public String getFormatKey() {
        return baseKey + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name()) ;
    }
}
