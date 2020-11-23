package com.cannontech.api.error.model;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/*
 * Details of API errors.
 */
public enum ApiErrorDetails {
    // Exceptions
    OBJECT_ALREADY_EXISTS(ApiErrorCategory.NONE.getCode() + 101, ApiErrorCategory.NONE, "Object Already Exists",
            "An identical object already exists."),
    AUTHENTICATION_INVALID(ApiErrorCategory.NONE.getCode() + 102, ApiErrorCategory.NONE, "Authentication Invalid", "Username or Password not valid"),

    // Validation Errors
    INVALID_VALUE(ApiErrorCategory.VALIDATION_FAILED.getCode() + 101, ApiErrorCategory.VALIDATION_FAILED, "Invalid Value",
            "Invalid value for the field."),
    MAX_LENGTH_EXCEEDED(ApiErrorCategory.VALIDATION_FAILED.getCode() + 102, ApiErrorCategory.VALIDATION_FAILED,
            "Max Length Exceeded", "Max length exceeded."),
    VALUE_OUTSIDE_VALID_RANGE(ApiErrorCategory.VALIDATION_FAILED.getCode() + 103, ApiErrorCategory.VALIDATION_FAILED, "Invalid Range", "Provided value is out of range"),
    ILLEGAL_CHARACTERS(ApiErrorCategory.VALIDATION_FAILED.getCode() + 104, ApiErrorCategory.VALIDATION_FAILED, "Illegal Characters", "Invalid characters");
    
    private ApiErrorCategory category;
    private int code;
    private String title;
    private String defaultMessage;

    private static final Map<Integer, ApiErrorDetails> validationFieldErrors;

    static {
        ImmutableMap.Builder<Integer, ApiErrorDetails> validationFieldErrorsBuilder = ImmutableMap.builder();
        for (ApiErrorDetails errorDetails : ApiErrorDetails.values()) {
            if (errorDetails.getCategory() == ApiErrorCategory.VALIDATION_FAILED) {
                validationFieldErrorsBuilder.put(errorDetails.code, errorDetails);
            }
        }
        validationFieldErrors = validationFieldErrorsBuilder.build();
    }

    ApiErrorDetails(int code, ApiErrorCategory category, String title, String defaultMessage) {
        this.code = code;
        this.category = category;
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

    public ApiErrorCategory getCategory() {
        return category;
    }

    public void setCategory(ApiErrorCategory category) {
        this.category = category;
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