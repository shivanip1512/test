package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;

@Configurable(value = "formatCronTagPrototype", autowire = Autowire.BY_NAME)
public class FormatCronTag extends YukonTagSupport {
    
    private CronExpressionTagService cronExpressionTagService;
    
    private String var;
    private boolean isVarSet = false;
    
    private String value;
    private boolean isValueSet = false; 
    
    @Override
    public void doTag() throws JspException, IOException {
        if (!isValueSet) { 
            throw new JspException("Value is not set.");
        }
        
        String formattedCron = cronExpressionTagService.getDescription(value, getUserContext());
        if(isVarSet) {
            getJspContext().setAttribute(var, formattedCron);
            return;
        }
        
        getJspContext().getOut().print(formattedCron);
    }
    
    public void setValue(String value) {
        this.value = value;
        this.isValueSet = true;
    }
    
    public void setVar(String var) {
        this.var = var;
        this.isVarSet = true;
    }
    
    @Autowired
    public void setCronExpressionTagService(CronExpressionTagService cronExpressionTagService) {
        this.cronExpressionTagService = cronExpressionTagService;
    }
    
}