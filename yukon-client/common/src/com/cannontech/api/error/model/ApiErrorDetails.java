package com.cannontech.api.error.model;

import java.util.Map;
import com.google.common.collect.ImmutableMap;

/*
 * Details of API errors.
 */
public enum ApiErrorDetails {
    // Global exceptions
    OBJECT_ALREADY_EXISTS(0, 102001, "Object Already Exists", "An identical object already exists."),
    AUTHENTICATION_INVALID(0, 102000, "Authentication Invalid", "Username or Password not valid"),
    // Validation Errors
    VALIDATION_FAILED(0, 101000, "Validation Failed", "Validation failed: see fields for details"),
    ILLEGAL_CHARACTERS(101000, 101001, "Illegal Characters", "Invalid characters"),
    VALUE_OUTSIDE_VALID_RANGE(101000, 101002, "Invalid Range", "Provided value is out of range"),
    MAX_LENGTH_EXCEEDED(101000, 101003, "Max Length Exceeded", "Max length exceeded"),
    INVALID_VALUE(101000, 101004, "Invalid Value", "Invalid value for the field");

    private int parentCode;
    private int code;
    private String title;
    private String defaultMessage;

    private static final Map<Integer, ApiErrorDetails> validationFieldErrors;

    static {
        ImmutableMap.Builder<Integer, ApiErrorDetails> validationFieldErrorsBuilder = ImmutableMap.builder();
        for (ApiErrorDetails errorDetails : ApiErrorDetails.values()) {
            if (errorDetails.getParentCode() == 101000) {
                validationFieldErrorsBuilder.put(errorDetails.code, errorDetails);
            }
        }
        validationFieldErrors = validationFieldErrorsBuilder.build();
    }

    ApiErrorDetails(int parentCode, int code, String title, String defaultMessage) {
        this.parentCode = parentCode;
        this.code = code;
        this.title = title;
        this.defaultMessage = defaultMessage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public int getParentCode() {
        return parentCode;
    }

    public void setParentCode(int parentCode) {
        this.parentCode = parentCode;
    }

    // This will return the type, url is fixed, with code appended
    public String getType() {
        return "/api/errors/" + getCode();
    }

    /**
     * Returns ApiErrorDetails for the specified code.
     */
    public static ApiErrorDetails getError(String childCode) {
        return validationFieldErrors.get(Integer.valueOf(childCode));
    }
}
