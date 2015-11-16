package com.cannontech.web.cc;

import java.util.Map;
import java.util.TreeMap;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public class ObjectConverter implements Converter, StateHolder {
    private Map<String, Object> cache = new TreeMap<String, Object>();

    public Object saveState(FacesContext facesContext) {
        return cache;
    }

    @SuppressWarnings("unchecked")
    public void restoreState(FacesContext facesContext, Object state) {
        cache = (Map<String, Object>) state;
    }

    public boolean isTransient() {
        return false;
    }

    public void setTransient(boolean arg0) {
    }

    public Object getAsObject(FacesContext facesContext, UIComponent arg1, String value) throws ConverterException {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return cache.get(value);
    }

    public String getAsString(FacesContext facesContext, UIComponent arg1, Object value) throws ConverterException {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            String valueString = (String) value;
            if (valueString.equals("")) {
                return null;
            }
        }
        // see if value is already in cache
        for (Map.Entry<String, Object> entry : cache.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        // otherwise, create new key and add to cache
        String key = RandomStringUtils.randomAlphanumeric(15);
        cache.put(key, (Object)value);
        return key;
    }

}
