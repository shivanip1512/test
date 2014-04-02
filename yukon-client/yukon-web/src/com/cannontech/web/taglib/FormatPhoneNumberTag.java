package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.service.PhoneNumberFormattingService;

@Configurable(value = "formatPhoneNumberTagPrototype", autowire = Autowire.BY_NAME)
public class FormatPhoneNumberTag extends YukonTagSupport {
    private PhoneNumberFormattingService formattingService;
    
    private String value;
    private boolean isValueSet = false; 
    
    private String var;
    private boolean isVarSet = false;
    
    private boolean htmlEscape;
    
    @Override
    public void doTag() throws JspException, IOException {
        if (!isValueSet) {
            throw new JspException("value is not set.");
        }
        
        String formattedPhoneNumber = formattingService.formatPhoneNumber(value, getUserContext());
        if (htmlEscape) {
            formattedPhoneNumber = StringEscapeUtils.escapeHtml4(formattedPhoneNumber);
        }
        
        if (isVarSet) {
            getJspContext().setAttribute(var, formattedPhoneNumber);
            return;
        }
        
        getJspContext().getOut().print(formattedPhoneNumber);
    }
    
    public void setValue(String value) {
        this.value = value;
        this.isValueSet = true;
    }
    
    public void setVar(String var) {
        this.var = var;
        this.isVarSet = true;
    }
    
    public void setHtmlEscape(boolean htmlEscape) {
        this.htmlEscape = htmlEscape;
    }

    @Autowired
    public void setFormattingService(
            PhoneNumberFormattingService formattingService) {
        this.formattingService = formattingService;
    }
    
}
