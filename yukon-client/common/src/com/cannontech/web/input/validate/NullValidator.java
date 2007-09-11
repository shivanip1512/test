package com.cannontech.web.input.validate;

import org.springframework.validation.Errors;

import com.cannontech.web.input.InputSource;

/**
 * Place-holder implementation of InputValidatore - does no validation
 */
public final class NullValidator<T> implements InputValidator<T> {
    private static final NullValidator NULL_VALIDATOR = new NullValidator();

    @SuppressWarnings("unchecked")
    public static final <T> NullValidator<T> getInstance() {
        return (NullValidator<T>) NULL_VALIDATOR;
    }

    public void validate(String path, InputSource field, T value, Errors errors) {
        // do nothing - there is no validation
    }
}