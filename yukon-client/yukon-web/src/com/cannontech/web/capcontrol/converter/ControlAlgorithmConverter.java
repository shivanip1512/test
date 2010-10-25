package com.cannontech.web.capcontrol.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.cannontech.capcontrol.ControlAlgorithm;

public class ControlAlgorithmConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
        ControlAlgorithm algorithm = ControlAlgorithm.valueOf(value);
        return algorithm;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
        ControlAlgorithm algorithm = (ControlAlgorithm)value;
        return algorithm.name();
    }

}