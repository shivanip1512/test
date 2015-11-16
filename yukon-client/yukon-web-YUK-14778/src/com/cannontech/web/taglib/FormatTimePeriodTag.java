package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.durationFormatter.DurationFormat;

@Configurable(value = "formatTimePeriodTagPrototype", autowire = Autowire.BY_NAME)
public class FormatTimePeriodTag extends YukonTagSupport {
    private DurationFormattingService durationFormattingService;
    
    private String var;
    private boolean isVarSet = false;
    
    private Date startDate;
    private boolean isStartDateSet = false; 

    private Date endDate;
    private boolean isEndDateSet = false; 
    
    private String type;
    private boolean isTypeSet = false;

    @Override
    public void doTag() throws JspException, IOException {
        if (!isStartDateSet) throw new JspException("start date is not set.");
        if (!isEndDateSet) throw new JspException("end date is not set.");
        if (!isTypeSet) throw new JspException("type is not set.");
        
        DurationFormat durationFormat = DurationFormat.valueOf(type);
        String formattedDuration = 
        	durationFormattingService.formatDuration(startDate, endDate, durationFormat, getUserContext());
        
        if (isVarSet) {
            getJspContext().setAttribute(var, formattedDuration);
            return;
        }
        
        getJspContext().getOut().print(formattedDuration);
    }
    
    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
        this.isStartDateSet = true;
    }
    
    public void setEndDate(final Date endDate) {
    	this.endDate = endDate;
    	this.isEndDateSet = true;
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
