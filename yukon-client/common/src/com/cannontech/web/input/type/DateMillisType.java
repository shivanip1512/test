package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.Date;

/**
 * Implementation of input type which represents a date input type.
 */
public class DateMillisType extends DefaultValidatedType<Long> {

    private String renderer = "dateType.jsp";
    private String format = "MM/dd/yyyy";

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Class<Long> getTypeClass() {
        return Long.class;
    }

    public PropertyEditor getPropertyEditor() {
        PropertyEditor millisPropEditor = new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                Long millis = Long.parseLong(text);
                setValue(millis);
            }
            @Override
            public String getAsText() {
                
                Long millis = (Long)getValue();
                
                if(millis == null){
                    Date nowDateTime = new Date();
                    millis = nowDateTime.getTime();
                }
                return millis.toString();
            }
        };
        return millisPropEditor;
    }

}
