package com.cannontech.web.input.validate;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

/**
 * Implementation of InputValidator that validates that a value exists for the
 * input
 */
public final class RequiredValidator implements InputValidator {

    public void validate(String path, String displayName, Object value, Errors errors) {

        if (value == null || StringUtils.isBlank(value.toString())) {
            errors.rejectValue(path,
                               "error.requiredField",
                               new Object[] { displayName },
                               "The input must have a value");
        }
    }

    public String getDescription() {
        return "required";
    }
}