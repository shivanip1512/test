package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.service.ContactNotificationFormattingService;
import com.cannontech.database.data.lite.LiteContactNotification;

@Configurable(value = "formatNotificationTagPrototype", autowire = Autowire.BY_NAME)
public class FormatNotificationTag extends YukonTagSupport {
    private ContactNotificationFormattingService formattingService;
    
    private LiteContactNotification value;
    private boolean isValueSet = false; 
    
    private String var;
    private boolean isVarSet = false;
    
    @Override
    public void doTag() throws JspException, IOException {
        if (!isValueSet) throw new JspException("value is not set.");
        String formattedNotification = formattingService.formatNotification(value, getUserContext());
                
        if (isVarSet) {
            getJspContext().setAttribute(var, formattedNotification);
            return;
        }
        
        getJspContext().getOut().print(formattedNotification);
    }
    
    public void setValue(LiteContactNotification value) {
        this.value = value;
        this.isValueSet = true;
    }
    
    public void setVar(String var) {
        this.var = var;
        this.isVarSet = true;
    }
    
    @Autowired
    public void setFormattingService(ContactNotificationFormattingService formattingService) {
        this.formattingService = formattingService;
    }
    
}
