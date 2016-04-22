package com.cannontech.web.i18n;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.servlet.tags.ThemeTag;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;

public class MsgTag extends ThemeTag {
    private static final Logger log = YukonLogManager.getLogger(MsgTag.class);
    
    @Override
    protected MessageSource getMessageSource() {
        try {
            YukonUserContextMessageSourceResolver messageSourceResolver = getMessageSourceResolver();
            YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(this.pageContext);
            MessageSource messageSource = messageSourceResolver.getMessageSource(yukonUserContext);
            return messageSource;
        } catch (RuntimeException e) {
            log.debug("Unable to get YukonUserContext from the Request.", e);
        }
        
        return super.getMessageSource();
    }
    
    public void setKey(Object key) {
        if (key instanceof MessageSourceResolvable) {
            setCode(null);
            setMessage((MessageSourceResolvable) key);
        } else if (key instanceof DisplayableEnum) {
            DisplayableEnum displayableEnum = (DisplayableEnum) key;
            setCode(displayableEnum.getFormatKey());
            setMessage(null);
        } else if (key instanceof String) {
            setCode((String) key);
            setMessage(null);
        } else if (key == null) {
            throw new IllegalArgumentException("Expected a String MessageSourceResolvable, got a null");
        } else {
            throw new IllegalArgumentException("Expected a String or MessageSourceResolvable, got a " + key.getClass().getName());
        }
    }
    
    private YukonUserContextMessageSourceResolver getMessageSourceResolver() {
        ApplicationContext context = this.getRequestContext().getWebApplicationContext();
        
        YukonUserContextMessageSourceResolver messageSourceResolver = 
            (YukonUserContextMessageSourceResolver) context.getBean("yukonUserContextMessageSourceResolver");
        
        return messageSourceResolver;
    }
    
    public void setArgument(Object argument) {
        setArguments(new Object[]{argument});
    }
    
}
