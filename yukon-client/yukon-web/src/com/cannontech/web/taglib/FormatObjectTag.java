package com.cannontech.web.taglib;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.i18n.ObjectFormattingService;

@Configurable(value = "formatObjectTagPrototype", autowire = Autowire.BY_NAME)
public class FormatObjectTag extends YukonTagSupport {

    private Object value;
    private String var;
    private boolean isVarSet = false;
    private ObjectFormattingService objectFormattingService;
    
    @Override
    public void doTag() throws IOException {
        String formattedObject = objectFormattingService.formatObjectAsString(value, getUserContext());
        
        if (isVarSet) {
            getJspContext().setAttribute(var, formattedObject);
        } else {
            getJspContext().getOut().print(formattedObject);
        }
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public void setVar(final String var) {
        this.var = var;
        this.isVarSet = true;
    }
    
    @Autowired
    public void setObjectFormattingService(ObjectFormattingService objectFormattingService) {
        this.objectFormattingService = objectFormattingService;
    }
    
}