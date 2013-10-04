package com.cannontech.common.util;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public enum EnabledStatus implements Displayable{
    ENABLED,
    DISABLED;

    @Override
    public MessageSourceResolvable getMessage() {
        return new YukonMessageSourceResolvable( "yukon.common.enabledStatus."  + name());
    }

}
