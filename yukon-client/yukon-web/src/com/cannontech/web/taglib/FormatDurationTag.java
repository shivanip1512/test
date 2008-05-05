package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.DurationFormattingService.DurationFormat;

@Configurable(value = "formatDurationTagPrototype", autowire = Autowire.BY_NAME)
public class FormatDurationTag extends YukonTagSupport {
    private DurationFormattingService durationFormattingService;
    
    private String var;
    private boolean isVarSet = false;
    
    private int value; //seconds
    private boolean isValueSet = false; 
    
    private String type;
    private boolean isTypeSet = false;

    @Override
    public void doTag() throws JspException, IOException {
        if (!isValueSet) throw new JspException("value is not set.");
        if (!isTypeSet) throw new JspException("type is not set.");
        
        DurationFormat durationFormat = DurationFormat.valueOf(type);
        String formattedDuration = durationFormattingService.formatDuration(value, durationFormat, getUserContext());
        
        if (isVarSet) {
            getJspContext().setAttribute(var, formattedDuration);
            return;
        }
        
        getJspContext().getOut().print(formattedDuration);
    }
    
    public void setValue(final int value) {
        this.value = value;
        this.isValueSet = true;
    }
    
    public void setType(final String type) {
        this.type = type;
        this.isTypeSet = true;
    }
    
    public void setVar(final String var) {
        this.var = var;
        this.isVarSet = true;
    }
    
    @Autowired
    public void setDurationFormattingService(DurationFormattingService durationFormattingService) {
        this.durationFormattingService = durationFormattingService;
    }
    
}
