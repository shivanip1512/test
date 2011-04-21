package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.jsp.JspException;

import org.joda.time.ReadableInstant;
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
    
    private long value; //milliseconds
    private boolean isValueSet = false; 
    
    private String type;
    private boolean isTypeSet = false;
    
    private Object startDate; // Date or ReadableInstant
    private boolean isStartDateSet = false;

    private Object endDate;  // Date or ReadableInstant
    private boolean isEndDateSet = false;
    
    @Override
    public void doTag() throws JspException, IOException {
    	boolean areDatesSet = (isStartDateSet && isEndDateSet);
        if (!isTypeSet) 
        	throw new JspException("type is not set.");
        
        DurationFormat durationFormat = DurationFormat.valueOf(type);
        
        String formattedDuration = null;
        if (areDatesSet){

            if (startDate instanceof Date &&
                endDate instanceof Date) {
                formattedDuration = durationFormattingService.formatDuration((Date)startDate, 
                                                                             (Date)endDate, 
                                                                             durationFormat, 
                                                                             getUserContext());

            } else if (startDate instanceof ReadableInstant && 
                        endDate instanceof ReadableInstant) {
                formattedDuration = 
                    durationFormattingService.formatDuration((ReadableInstant)startDate, 
                                                             (ReadableInstant)endDate, 
                                                             durationFormat, 
                                                             getUserContext());
            }
        } else {
            if (isValueSet){
                formattedDuration = durationFormattingService.formatDuration(value, TimeUnit.MILLISECONDS, durationFormat, getUserContext());
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
    
    public void setValue(long value) {
        this.value = value;
        this.isValueSet = true;
    }
    
    public void setStartDate(Object startDate) {
    	this.startDate = startDate;
    	this.isStartDateSet = true;
    }
    
    public void setEndDate(Object endDate) {
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
