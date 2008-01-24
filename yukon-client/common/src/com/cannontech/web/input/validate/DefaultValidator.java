package com.cannontech.web.input.validate;

import org.springframework.validation.Errors;

/**
 * InputValidator implementation that does no validation
 */
public class DefaultValidator<T> implements InputValidator<T> {

    public static <T> InputValidator<T> getInstance() {
        return new DefaultValidator<T>();
    }

    public String getDescription() {
        return "";
    }

    public void validate(String path, String displayName, Object value, Errors errors) {
        // do nothing - no validation
    }

}
