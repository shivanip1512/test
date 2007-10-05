package com.cannontech.web.input.validate;

import org.springframework.validation.Errors;

/**
 * InputValidator implementation that does no validation
 */
public class DefaultValidator implements InputValidator {

    public static DefaultValidator defaultInstance = new DefaultValidator();

    public static InputValidator getInstance() {
        return defaultInstance;
    }

    public String getDescription() {
        return "";
    }

    public void validate(String path, String displayName, Object value, Errors errors) {
        // do nothing - no validation
    }

}
