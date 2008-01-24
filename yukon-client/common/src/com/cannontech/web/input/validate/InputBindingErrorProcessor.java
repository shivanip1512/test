package com.cannontech.web.input.validate;

import java.util.Map;

import org.springframework.validation.DefaultBindingErrorProcessor;

import com.cannontech.web.input.InputSource;

/**
 * Extension of DefaultBindingErrorProcessor which is used to get the bind error
 * arguments for an input.
 */
public class InputBindingErrorProcessor extends DefaultBindingErrorProcessor {

    private Map<String, ? extends InputSource<?>> inputMap = null;

    public InputBindingErrorProcessor(Map<String, ? extends InputSource<?>> inputMap) {
        this.inputMap = inputMap;
    }

    @Override
    protected Object[] getArgumentsForBindError(String objectName, String field) {

        if (inputMap.containsKey(field)) {
            InputSource<?> input = inputMap.get(field);

            return new Object[] { input.getDisplayName() };
        }

        return new Object[] {};

    }

}
