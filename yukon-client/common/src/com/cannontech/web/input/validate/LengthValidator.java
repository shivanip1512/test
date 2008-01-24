package com.cannontech.web.input.validate;

import org.springframework.validation.Errors;

/**
 * Implementation of InputValidator that validates that a value is longer than
 * or equal to min length and shorter than or equal to max length. Assumes there
 * is a useful toString for the value being checked.
 */
public final class LengthValidator implements InputValidator<Object> {

    private int minLength = 0;
    private int maxLength = Integer.MAX_VALUE;

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void validate(String path, String displayName, Object value,
            Errors errors) {

        // Only validate if a value exists
        if (value == null) {
            return;
        }

        String valueString = value.toString();

        if (valueString.length() < minLength) {
            errors.rejectValue(path,
                               "yukon.web.input.error.minLength",
                               new Object[] { displayName, minLength },
                               "The value must be at least " + minLength + " characters long.");

        }

        if (valueString.length() > maxLength) {
            errors.rejectValue(path,
                               "yukon.web.input.error.maxLength",
                               new Object[] { displayName, maxLength },
                               "The value must not be longer than " + maxLength + " characters.");

        }

    }

    public String getDescription() {
        return "length: " + minLength + " - " + maxLength;
    }
}
