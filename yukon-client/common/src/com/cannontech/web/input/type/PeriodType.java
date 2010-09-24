package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.PeriodFormatter;

import com.cannontech.common.util.SimplePeriodFormat;

/**
 * Implementation of input type which represents an Instant type.
 */
public class PeriodType extends DefaultValidatedType<Period> {

    private String renderer = "stringType.jsp";
    private PeriodFormatter periodFormatter = SimplePeriodFormat.getConfigPeriodFormatter();

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public Class<Period> getTypeClass() {
        return Period.class;
    }

    public PropertyEditor getPropertyEditor() {
    	
    	PropertyEditor propertyEditor = new PropertyEditorSupport() {
    		
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
            	
            	Period period = periodFormatter.parsePeriod(text);
                setValue(period);
            }
            @Override
            public String getAsText() {
            	
                ReadablePeriod period = (ReadablePeriod)getValue();
            	String instantStr = periodFormatter.print(period);
            	return instantStr;
            }
        };
        
        return propertyEditor;
    }
}
