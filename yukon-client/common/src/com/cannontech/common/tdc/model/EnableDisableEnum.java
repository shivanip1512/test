package com.cannontech.common.tdc.model;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public enum EnableDisableEnum implements Displayable{
    ENABLED,
    DISABLED;

    @Override
    public MessageSourceResolvable getMessage() {
        return new YukonMessageSourceResolvable( "yukon.web.modules.tools.tdc.enableDisableEnum."  + name());
    }

}
