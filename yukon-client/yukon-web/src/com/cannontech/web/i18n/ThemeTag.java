package com.cannontech.web.i18n;

import javax.servlet.jsp.JspException;

import org.springframework.context.NoSuchMessageException;


public class ThemeTag extends org.springframework.web.servlet.tags.ThemeTag {
    private boolean url;
    
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
