package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import org.joda.time.LocalTime;

public class LocalTimeType extends DefaultValidatedType<LocalTime>{
    
    private String renderer = "stringType.jsp";

    @Override
    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    @Override
    public Class<LocalTime> getTypeClass() {
        return LocalTime.class;
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        
        PropertyEditor propertyEditor = new PropertyEditorSupport() {
            @Override
            public String getAsText() {
                return ((LocalTime) getValue()).toString();
            }
            @Override
            public void setAsText(String text) {
                LocalTime t = LocalTime.parse(text);
                setValue(t);
            }
        };
        
        return propertyEditor;
    }
}
