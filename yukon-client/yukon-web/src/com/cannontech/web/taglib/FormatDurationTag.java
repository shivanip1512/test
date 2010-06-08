package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.durationFormatter.DurationFormat;

@Configurable(value = "formatDurationTagPrototype", autowire = Autowire.BY_NAME)
public class FormatDurationTag extends YukonTagSupport {
    private DurationFormattingService durationFormattingService;
    
    private String var;
    private boolean isVarSet = false;
    
    private int value; //milliseconds
    private boolean isValueSet = false; 
    
    private String type;
    private boolean isTypeSet = false;
    
    private Date startDate;
    private boolean isStartDateSet = false;

    private Date endDate;
    private boolean isEndDateSet = false;
    
    private Boolean roundRightmostUp = null;

    @Override
    public void doTag() throws JspException, IOException {
    	boolean areDatesSet = (isStartDateSet && isEndDateSet);
        if (!isTypeSet) 
        	throw new JspException("type is not set.");
        
        DurationFormat durationFormat = DurationFormat.valueOf(type);
        
        // use durationFormat defaulting rounding if roundRightmostUp parameter was not used
        boolean useRoundRightmostUp = durationFormat.getRoundRightmostUpDefault();
        if (roundRightmostUp != null) {
        	useRoundRightmostUp = roundRightmostUp.booleanValue();
        }
        
        String formattedDuration;
        if (areDatesSet){
        	formattedDuration = durationFormattingService.formatDuration(startDate, endDate, durationFormat, useRoundRightmostUp, getUserContext());
        } else {
            if (isValueSet){
                formattedDuration = durationFormattingService.formatDuration(value, TimeUnit.MILLISECONDS, durationFormat, useRoundRightmostUp, getUserContext());
            } else {
                throw new JspException("both possible value types were not set");
            }
        }
        
        if (isVarSet) {
            getJspContext().setAttribute(var, formattedDuration);
            return;
        }
        
        getJspContext().getOut().print(formattedDuration);
    }
    
    public void setValue(int value) {
        this.value = value;
        this.isValueSet = true;
    }
    
    public void setStartDate(Date startDate) {
    	this.startDate = startDate;
    	this.isStartDateSet = true;
    }
    
    public void setEndDate(Date endDate) {
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
    
    public void setRoundRightmostUp(Boolean roundRightmostUp) {
		this.roundRightmostUp = roundRightmostUp;
	}
    
    @Autowired
    public void setDurationFormattingService(DurationFormattingService durationFormattingService) {
        this.durationFormattingService = durationFormattingService;
    }
    
}
