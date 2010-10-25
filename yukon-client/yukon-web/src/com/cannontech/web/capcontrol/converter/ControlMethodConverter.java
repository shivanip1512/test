package com.cannontech.web.capcontrol.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.cannontech.capcontrol.ControlMethod;

public class ControlMethodConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
        ControlMethod algorithm = ControlMethod.valueOf(value);
        return algorithm;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
        ControlMethod algorithm = (ControlMethod)value;
        return algorithm.name();
    }

}