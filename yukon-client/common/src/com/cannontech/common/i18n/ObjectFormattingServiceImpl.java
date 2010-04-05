package com.cannontech.common.i18n;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class ObjectFormattingServiceImpl implements ObjectFormattingService {
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private TemplateProcessorFactory templateProcessorFactory;

    @Override
    public String formatObjectAsString(Object value, YukonUserContext userContext) {
        MessageSourceResolvable resolvable = formatObjectAsResolvable(value, userContext);
        String result = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(resolvable);
        return result;
    }
    
    @Override
    public MessageSourceResolvable formatObjectAsResolvable(Object object, YukonUserContext userContext) {
        if (object instanceof MessageSourceResolvable) {
            return (MessageSourceResolvable) object;
        }
        if (object instanceof DisplayableEnum) {
            return new YukonMessageSourceResolvable(((DisplayableEnum) object).getFormatKey());
        }
        if (object instanceof ResolvableTemplate) {
            String string = templateProcessorFactory.processResolvableTemplate((ResolvableTemplate) object, userContext);
            return YukonMessageSourceResolvable.createDefaultWithoutCode(string);
        }
        return YukonMessageSourceResolvable.createDefaultWithoutCode(ObjectUtils.toString(object));
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
