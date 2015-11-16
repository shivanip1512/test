package com.cannontech.web.i18n;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;


public class ThemeTag extends org.springframework.web.servlet.tags.ThemeTag {
    private static final Logger log = YukonLogManager.getLogger(ThemeTag.class);
    private boolean url;
    
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
    
    @Override
    protected String resolveMessage() throws JspException, NoSuchMessageException {
        String resolvedMessage = super.resolveMessage();
        if (url) {
            if (resolvedMessage.startsWith("/")) {
                return getRequestContext().getContextPath() + resolvedMessage;
            }
        }      
        return resolvedMessage;
    }

    private YukonUserContextMessageSourceResolver getMessageSourceResolver() {
        ApplicationContext context = this.getRequestContext().getWebApplicationContext();
        
        YukonUserContextMessageSourceResolver messageSourceResolver = 
            (YukonUserContextMessageSourceResolver) context.getBean("yukonUserContextMessageSourceResolver");
        
        return messageSourceResolver;
    }
    
    public void setKey(String key) {
        setCode(key);
    }
    
    public void setUrl(boolean url) {
        this.url = url;
    }
    
    public boolean isUrl() {
        return this.url;
    }
    
    public void setDefault(String defaultText) {
        setText(defaultText);
    }
}
