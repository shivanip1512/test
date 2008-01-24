package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;

/**
 * Implementation of input type which represents a date input type.
 */
public class DateType extends DefaultValidatedType<Date> {

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

    public Class<Date> getTypeClass() {
        return Date.class;
    }

    public PropertyEditor getPropertyEditor() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return new CustomDateEditor(simpleDateFormat, true);
    }

}
