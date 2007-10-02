package com.cannontech.web.input.type;

import java.beans.PropertyEditor;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;

import com.cannontech.web.input.validate.InputValidator;
import com.cannontech.web.input.validate.NullValidator;

/**
 * Implementation of input type which represents a date input type.
 */
public class DateType implements InputType<Date> {

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

    public InputValidator<Date> getValidator() {
        return NullValidator.getInstance();
    }

    public PropertyEditor getPropertyEditor() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return new CustomDateEditor(simpleDateFormat, true);
    }

}
