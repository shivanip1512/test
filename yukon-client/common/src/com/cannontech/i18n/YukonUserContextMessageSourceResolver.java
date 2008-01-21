package com.cannontech.i18n;

import org.springframework.context.MessageSource;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.user.YukonUserContext;

public interface YukonUserContextMessageSourceResolver {
    public MessageSource getMessageSource(YukonUserContext userContext);
    public MessageSourceAccessor getMessageSourceAccessor(YukonUserContext userContext);
}
