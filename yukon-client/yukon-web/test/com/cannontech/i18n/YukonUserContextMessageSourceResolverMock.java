package com.cannontech.i18n;

import org.apache.commons.lang3.LocaleUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.user.YukonUserContext;

public class YukonUserContextMessageSourceResolverMock implements
    YukonUserContextMessageSourceResolver, MessageSourceAware {

    private MessageSource messageSource;

    @Override
    public MessageSource getMessageSource(YukonUserContext userContext) {
        return messageSource;
    }

    @Override
    public MessageSourceAccessor getMessageSourceAccessor(YukonUserContext userContext) {
        return new MessageSourceAccessor(messageSource, LocaleUtils.toLocale("en_US"));
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
