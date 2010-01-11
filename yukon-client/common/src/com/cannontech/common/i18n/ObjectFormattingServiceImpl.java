package com.cannontech.common.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class ObjectFormattingServiceImpl implements ObjectFormattingService {
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private TemplateProcessorFactory templateProcessorFactory;

    @Override
    public String formatObjectAsValue(Object value, YukonUserContext userContext) {
        Object result = formatNonStringPortion(value, userContext);
        if (result == null) {
            throw new IllegalArgumentException("input resolved to null");
        }
        return result.toString();
    }

    private Object formatNonStringPortion(Object value, YukonUserContext userContext) {
        if (value instanceof MessageSourceResolvable) {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            return messageSourceAccessor.getMessage((MessageSourceResolvable) value);
        }
        if (value instanceof DisplayableEnum) {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            DisplayableEnum displayableEnum = (DisplayableEnum) value;
            return messageSourceAccessor.getMessage(displayableEnum);
        }
        if (value instanceof ResolvableTemplate) {
            return templateProcessorFactory.processResolvableTemplate((ResolvableTemplate) value,
                                                                      userContext);
        }
        return value;
    }

    @Override
    public String formatObjectAsKey(Object value, YukonUserContext userContext) {
        if (value instanceof String) {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            return messageSourceAccessor.getMessage((String) value);
        }
        Object result = formatNonStringPortion(value, userContext);
        if (result instanceof String) {
            return result.toString();
        }
        throw new IllegalArgumentException("input was not a legal key");
    }

    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }

    @Autowired
    public void setTemplateProcessorFactory(TemplateProcessorFactory templateProcessorFactory) {
        this.templateProcessorFactory = templateProcessorFactory;
    }

}
