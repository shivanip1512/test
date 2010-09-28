package com.cannontech.web.input;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessorFactory;

import com.cannontech.web.input.type.InputType;

public class InputUtil {

    public static void applyProperties(InputRoot inputRoot, Object task, Map<String, String> properties) throws BeansException {
        Map<String, ? extends InputSource<?>> inputs = inputRoot.getInputMap();
        for (Entry<String, ? extends InputSource<?>> entry : inputs.entrySet()) {
            String inputProperty = properties.get(entry.getKey());
            if (inputProperty != null) {
                BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(task);
                InputType<?> type = entry.getValue().getType();
                PropertyEditor propertyEditor = type.getPropertyEditor();
                beanWrapper.registerCustomEditor(type.getTypeClass(), propertyEditor);
                beanWrapper.setPropertyValue(entry.getKey(), inputProperty);
            }
        }
    }

    public static Map<String, String> extractProperties(InputRoot inputRoot, Object task) throws BeansException {
        HashMap<String,String> properties = new HashMap<String,String>();
        Map<String, ? extends InputSource<?>> inputs = inputRoot.getInputMap();
        BeanWrapper beanWrapper = new BeanWrapperImpl(task);
        for (Entry<String, ? extends InputSource<?>> entry : inputs.entrySet()) {
            String field = entry.getKey();
            InputType<?> type = entry.getValue().getType();
            Object propertyValue = beanWrapper.getPropertyValue(field);
            if (propertyValue != null) {
                PropertyEditor propertyEditor = type.getPropertyEditor();
                propertyEditor.setValue(propertyValue);
                String inputProperty = propertyEditor.getAsText();
                properties.put(field, inputProperty);
            }
        }
        return properties;
    }

}
