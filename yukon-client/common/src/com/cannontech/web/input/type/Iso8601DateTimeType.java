package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.cannontech.common.util.Iso8601DateUtil;

/**
 * Implementation of input type which represents a date input type.
 */
public class Iso8601DateTimeType extends DefaultValidatedType<DateTime> {

    private String renderer = "stringType.jsp";

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public Class<DateTime> getTypeClass() {
        return DateTime.class;
    }

    public PropertyEditor getPropertyEditor() {
    	
    	PropertyEditor propertyEditor = new PropertyEditorSupport() {
    		
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
            	
            	DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis();
            	DateTime dateTime = formatter.parseDateTime(text);
                setValue(dateTime);
            }
            @Override
            public String getAsText() {
                
            	DateTime dateTime = (DateTime)getValue();
            	return Iso8601DateUtil.formatIso8601Date(dateTime.toDate());
            }
        };
        
        return propertyEditor;
    }
}
