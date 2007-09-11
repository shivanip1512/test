package com.cannontech.web.input.validate;

import org.springframework.validation.Errors;

import com.cannontech.web.input.InputSource;

/**
 * Interface used to validate and input.
 * @param <T> - Java type of input value to validate
 */
public interface InputValidator<T> {

    /**
     * Method used to check to make sure a value is valid for a given input.
     * @param path - Path to the value
     * @param field - Input to validate the value for
     * @param value - Value to be validated
     * @param errors - Spring errors object used if validation fails
     */
    public void validate(String path, InputSource field, T value, Errors errors);

}
