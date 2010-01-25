package com.cannontech.common.i18n;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.user.YukonUserContext;

public interface ObjectFormattingService {
    public String formatObjectAsString(Object object, YukonUserContext userContext);
    public MessageSourceResolvable formatObjectAsResolvable(Object object, YukonUserContext userContext);
}
