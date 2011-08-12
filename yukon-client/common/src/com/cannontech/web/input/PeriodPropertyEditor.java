package com.cannontech.web.input;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;


public class PeriodPropertyEditor extends PropertyEditorSupport {
    private PeriodFormatter periodFormatter = ISOPeriodFormat.standard();
    
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if(StringUtils.isNotBlank(text)) {
            Period period = periodFormatter.parsePeriod(text);
            setValue(period);
        }
    }
    
    @Override
    public String getAsText() {
        Period period = (Period) getValue();
        
        return period==null ? "" : period.toString();
    }

}
