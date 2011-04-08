package com.cannontech.common.i18n;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

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

    @Override
    public <T extends DisplayableEnum> List<T> sortEnumValues(T[] enumList,
                                                              final T first, final T last,
                                                              YukonUserContext context) {
        final MessageSourceAccessor messageSourceAccessor =
            messageSourceResolver.getMessageSourceAccessor(context);
        List<T> retVal = Lists.newArrayList(enumList);
        Collections.sort(retVal, new Comparator<T>() {
            @Override
            public int compare(T t1, T t2) {
                if (t1 == t2)
                    return 0;
                if (t1 == first || t2 == last) {
                    return -1;
                }
                if (t1 == last || t2 == first) {
                    return 1;
                }
                String localName1 = messageSourceAccessor.getMessage(t1.getFormatKey());
                String localName2 = messageSourceAccessor.getMessage(t2.getFormatKey());
                return localName1.compareToIgnoreCase(localName2);
            }
        });
        return retVal;
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
