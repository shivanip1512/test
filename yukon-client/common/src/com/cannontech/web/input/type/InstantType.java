package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Implementation of input type which represents an Instant type.
 */
public class InstantType extends DefaultValidatedType<Instant> {

    private String renderer = "stringType.jsp";

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public Class<Instant> getTypeClass() {
        return Instant.class;
    }

    public PropertyEditor getPropertyEditor() {
    	
    	PropertyEditor propertyEditor = new PropertyEditorSupport() {
    		
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
            	
            	DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
                DateTime dateTime = formatter.parseDateTime(text);
                Instant instant = dateTime.toInstant();
                setValue(instant);
            }
            @Override
            public String getAsText() {
            	
            	DateTimeFormatter formatter = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);
            	ReadableInstant instant = (ReadableInstant)getValue();
            	String instantStr = formatter.print(instant);
            	return instantStr;
            }
        };
        
        return propertyEditor;
    }
}
