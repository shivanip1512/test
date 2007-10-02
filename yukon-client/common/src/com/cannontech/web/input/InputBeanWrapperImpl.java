package com.cannontech.web.input;

import java.beans.PropertyEditor;

import org.springframework.beans.BeanWrapperImpl;

/**
 * BeanWrapper implementation for use with Inputs
 */
public class InputBeanWrapperImpl extends BeanWrapperImpl {

    public InputBeanWrapperImpl(Object obj) {
        super(obj);
    }

    /**
     * Method to get the value of a property as text using the registered
     * property editor for the field
     * @param path - Path to property
     * @return The formatted string value of the property
     */
    public String getValueAsText(String path) {

        Object value = getPropertyValue(path);

        if (value == null) {
            return null;
        }

        PropertyEditor editor = findCustomEditor(Object.class, path);
        if (editor != null) {

            // Format the value
            editor.setValue(value);
            String formattedValue = editor.getAsText();

            return formattedValue;
        }

        return value.toString();
    }

}
